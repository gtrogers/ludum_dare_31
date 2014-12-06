(ns crate-expectations.mobs
  (:require [play-clj.core :refer [key-pressed?]]
            [crate-expectations.world :as world]))

(defn- decelerate [v]
  (let [new-v (* world/deceleration v)]
    (if (< (Math/abs new-v) world/damping)
      0
      new-v)))

(defn mob-data [x y tag]
  (assoc {:angle 0
          :x x
          :y y 
          :x-velocity 0
          :y-velocity 0
          :on-floor? false
          }
         tag true))

(defn- clamp [n lower upper]
  (cond
    (> n upper) upper
    (< n lower) lower
    :default    n
    ))

(defn- get-x-velocity [e] 
  (cond
    (key-pressed? :left)  (* -1 world/max-velocity)
    (key-pressed? :right) world/max-velocity
    :default              (:x-velocity e)
    ))

(defn- get-y-velocity [{:keys [y-velocity on-floor?]}] 
  (cond
    (key-pressed? :up)   (if (and on-floor? (> 0 y-velocity)) world/jump-velocity y-velocity)
    :default             y-velocity 
    ))

(defn move [{:keys [delta-time]} {:keys [x y] :as e}]
  (let [x-velocity (get-x-velocity e)
        y-velocity (+ (get-y-velocity e) world/gravity) 
        delta-x (* delta-time x-velocity)
        delta-y (* delta-time y-velocity)]
    (assoc e
           :x (+ x delta-x)
           :y (+ y delta-y) 
           :x-velocity (decelerate x-velocity)
           :y-velocity (decelerate y-velocity) 
           )))

(defn clip [{:keys [x y] :as e}]
  (let [new-y (clamp y 0 world/height)]
  (assoc e
         :x (clamp x 0 world/width)
         :y new-y
         :on-floor? (= new-y 0))))
