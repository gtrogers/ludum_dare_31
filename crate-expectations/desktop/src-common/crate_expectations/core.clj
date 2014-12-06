(ns crate-expectations.core
  (:require [play-clj.core :refer :all]
            [play-clj.ui :refer :all]
            [play-clj.g2d :refer :all]
            [crate-expectations.world :as world]
            [crate-expectations.mobs :as mobs]
            [crate-expectations.platforms :as platforms]
            [crate-expectations.crates :as crates]
            ))

(declare crate-expectations main-screen error-screen)

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (update! screen :camera (orthographic):renderer (stage))
    (add-timer! screen :spawn-crate 1 2)
    [
     (texture "test_background.png")
     (merge (shape :filled :rect 0 0 16 32 :set-color (color :green))
            (mobs/mob-data 0 0 :player)) 
     (merge (shape :filled :rect 0 0 64 8 :set-color (color :white))
            (platforms/platform-data 0 80 64 8 :platform-1))
     (merge (shape :filled :rect 0 0 64 8 :set-color (color :white))
            (platforms/platform-data 320 80 64 8 :platform-2))
     ])

  :on-render
  (fn [screen entities]
    (clear!)
    (->> 
      (map (fn [entity]
             (->> (mobs/move screen entity)
                  (mobs/clip entities)
                  (crates/flash-crate! screen)
                  crates/arm-crate!
                  crates/open-crate! 
                  )) entities) 
      (remove :destroy!)
      (render! screen)) 
    )

  :on-key-down
  (fn [screen entities]
    (cond 
      (key-pressed? :r) (on-gl  (set-screen! crate-expectations main-screen)) 
      
      ) 
    )

  :on-resize
  (fn [screen entities] (height! screen world/height))

  :on-timer
  (fn [screen entities]
    (case (:id screen)
      :spawn-crate (crates/spawn entities))))

(defscreen error-screen
  :on-render
  (fn  [screen entities]
    (clear! 0.5 0.5 0.5 1.0)))

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
