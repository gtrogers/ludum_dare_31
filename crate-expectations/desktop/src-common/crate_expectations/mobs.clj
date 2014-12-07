(ns crate-expectations.mobs
  (:require [play-clj.core :refer [key-pressed?]]
            [play-clj.math :refer [rectangle rectangle!]]
            [crate-expectations.world :as world]
            [crate-expectations.platforms :as platforms]))

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
          :mob? true
          }
         tag true))

(defn- clamp [n lower upper]
  (cond
    (> n upper) upper
    (< n lower) lower
    :default    n
    ))

(defn- get-x-velocity [tag e]
  (if (tag e)
    (cond
      (key-pressed? :a)  (* -1 world/max-velocity)
      (key-pressed? :d) world/max-velocity
      :default              (:x-velocity e)
      )
    (:x-velocity e)
    ))

(defn- get-y-velocity [tag {:keys [y-velocity on-floor?] :as e}] 
  (if (tag e)
    (cond
      (key-pressed? :w)   (if (and on-floor? (> 0 y-velocity)) world/jump-velocity y-velocity)
      :default             y-velocity 
      )
    (:y-velocity e)
    ))

(defn move [{:keys [delta-time]} {:keys [x y] :as e}]
  (if (:mob? e)
    (let [x-velocity (get-x-velocity :player? e)
          y-velocity (+ (get-y-velocity :player? e) world/gravity) 
          delta-x (* delta-time x-velocity world/pixels-per-move)
          delta-y (* delta-time y-velocity world/pixels-per-move)]
      (assoc e
             :x (+ x delta-x)
             :y (+ y delta-y) 
             :x-velocity (decelerate x-velocity)
             :y-velocity (decelerate y-velocity) 
             :delta-x delta-x
             :delta-y delta-y
             ))
    e))

(defn clip [entities {:keys [x y delta-x delta-y] :as e}]
  (if (:mob? e)
    (let [old-x (- x delta-x)
          old-y (- y delta-y)
          on-platform? (platforms/on-platform? entities old-x old-y)
          new-y (clamp y world/base world/height)
          down? (> old-y new-y)
          used-y (if (and down? on-platform?) old-y new-y)
          used-x (clamp x 0 world/width)
          ;;hit-box (when-let [hb (:hit-box e)] (rectangle! hb :set-position used-x (+ 56 used-y))) ;; TODO record hitbox height offset on entities
          ]
      (assoc e
             :x used-x
             :y used-y 
             :on-floor? (or on-platform? (= used-y world/base))
             ;;:hit-box hit-box
             ))
    e
    ))
