(ns crate-expectations.bullets
  (:require [play-clj.g2d :refer [texture]] 
            [play-clj.core :refer [key-pressed?]]
            [play-clj.math :refer [rectangle]]
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

(defn update-bullet [{:keys [bullet? x-velocity x] :as e}]
  (if bullet? (assoc e :x (+ x x-velocity)) e))
