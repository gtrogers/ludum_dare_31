(ns crate-expectations.bullets
  (:require [play-clj.g2d :refer [texture]] 
            [play-clj.core :refer [key-pressed? find-first]]
            [play-clj.math :refer [rectangle rectangle!]]
            [crate-expectations.mobs :as mobs]
            [crate-expectations.world :as world]
            ))

(defn bullet-data [x y x-velocity y-velocity tag]
  (assoc {:x x
          :y y
          :x-velocity x-velocity 
          :y-velocity y-velocity
          :bullet? true
          :hit-box (rectangle x y 8 8)
          } tag true))

(defn bullet [x y x-velocity y-velocity tag]
  (merge (texture "coal.png") 
         (bullet-data x y x-velocity y-velocity tag)))

(defn spawn [{:keys [x y] :as player}]
  (let [bullet-height (+ y 18)]
  (cond (key-pressed? :left)  (bullet x bullet-height (* -1 world/bullet-speed) 0 :spawned-bullet)
        (key-pressed? :right) (bullet (+ x 16) bullet-height world/bullet-speed 0 :spawned-bullet) 
        )))

(defn- move [{:keys [x x-velocity] :as bullet}]
    (assoc bullet :x (+ x x-velocity))
  )

(defn- overlap? [r1 r2]
  (rectangle! r1 :overlaps r2)
  )

(defn- remove-collisions! [entities bullet]
  (let [ hit (find-first
               (fn [{:keys [hit-box enemy?] :as e}]
                 (when (and hit-box enemy?)
                   (overlap? (:hit-box bullet) hit-box))) entities)]
    (when-not hit bullet)
    )
  )

(defn remove-offscreen-bullet! [{:keys [x y] :as bullet}]
  (when-not (world/off-screen x y) bullet))

(defn update-bullet [entities {:keys [bullet? x-velocity x] :as e}]
  (if bullet? 
    (some->> (move e)
             (remove-offscreen-bullet!)
             (remove-collisions! entities))
    e))
