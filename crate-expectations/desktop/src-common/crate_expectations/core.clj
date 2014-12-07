(ns crate-expectations.core
  (:require [play-clj.core :refer :all]
            [play-clj.ui :refer :all]
            [play-clj.g2d :refer :all]
            [play-clj.math :refer :all]
            [crate-expectations.world :as world]
            [crate-expectations.mobs :as mobs]
            [crate-expectations.platforms :as platforms]
            [crate-expectations.crates :as crates]
            [crate-expectations.enemies :as enemies]
            [crate-expectations.bullets :as bullets]
            ))

(declare crate-expectations main-screen error-screen game-over-screen)

(defn- update-hit-box [{:keys [hit-box x y hit-box-offsets] :as e}]
  (if hit-box
    (let [x-offset (cond
                     hit-box-offsets (:x hit-box-offsets)
                     :default        0)
          y-offset (cond
                     hit-box-offsets (:y hit-box-offsets)
                     :default        0)
          ]
       (update-in e [:hit-box] #(rectangle! % :set-position (+ x x-offset) (+ y y-offset)))) 
    e
    )
  )

(defn- spawn-and-destroy [entities]
  (map (fn [{:keys [x y spawn-and-destroy!] :as e}]
         (if spawn-and-destroy!
           (enemies/enemy x y :test)
           e))
       entities))

(defn- spawn-bullets [entities]
  (if-let [bullet (bullets/spawn (find-first :player? entities))]
    (concat entities [bullet]) ;; makes sure bullet is last because entities is lazy at this point
    entities
    )
  )

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (update! screen :camera (orthographic):renderer (stage))
    (add-timer! screen :spawn-crate 1 2)
    (add-timer! screen :spawn-bullets 0 0.2)
    [
     (texture "warehouse.png")
     (mobs/player 200 100 :player? screen)
     (merge (texture "platform_1.png")
            (platforms/platform-data 0 100 64 8 :platform-1))
     (merge (texture "platform_1.png")
            (platforms/platform-data 336 100 64 8 :platform-2))
     (merge (texture "platform_2.png")
            (platforms/platform-data 128 180 32 8 :floating-platform?))
     ])

  :on-render
  (fn [screen entities]
    (clear!)
    (when (find-first :game-over? entities) (set-screen! crate-expectations game-over-screen))
    (->> 
      (map (fn [entity]
             (->> (mobs/move screen entity)
                  (mobs/clip entities)
                  (crates/flash-crate! screen)
                  crates/arm-crate!
                  crates/open-crate! 
                  (enemies/logic (find-first :player? entities) screen)
                  (enemies/bullet-collisions entities)
                  (bullets/update-bullet entities) 
                  (mobs/player-collisions entities)
                  update-hit-box
                  platforms/move-floating-platform
                  )) entities)
      spawn-and-destroy
      (render! screen)) 
    )

  :on-key-down
  (fn [screen entities]
    (cond 
      (key-pressed? :r) (on-gl  (set-screen! crate-expectations main-screen)) 
      (key-pressed? :p) (prn (find-first :player? entities)) 
      
      ) 
    )

  :on-resize
  (fn [screen entities] (height! screen world/height))

  :on-timer
  (fn [screen entities]
    (case (:id screen)
      :spawn-crate   (crates/spawn entities) 
      :spawn-bullets (spawn-bullets entities)
      )))

(defscreen error-screen
  :on-render
  (fn  [screen entities]
    (clear! 0.5 0.5 0.5 1.0)))

(defscreen game-over-screen
  :on-render
  (fn  [screen entities]
    (clear! 0 0.5 0.5 1.0))
  
  :on-key-down
  (fn [screen entities]
    (cond 
      (key-pressed? :space) (set-screen! crate-expectations main-screen)) 
    )
  )

(defgame crate-expectations
  :on-create
  (fn [this]
    (set-screen! this main-screen)))

;; TODO: better error handling
;; Run this to reset --> (on-gl  (set-screen! crate-expectations main-screen)))

(set-screen-wrapper!
  (fn  [screen screen-fn]
        (clear!)
    (try  (screen-fn)
         (catch Exception e
           (.printStackTrace e)
           (set-screen! crate-expectations error-screen)))))
