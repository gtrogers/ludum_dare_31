(ns crate-expectations.platforms
  (:require [play-clj.math :refer [rectangle rectangle!]]
            [crate-expectations.world :as world]
            ))

(defn platform-data [x y width height tag]
  (assoc {:x x
          :y y
          :hit-box (rectangle x y width height)
          :platform? true
          } tag true))

(defn- platform-test [e x y]
  ;; TODO use rectangle for platform collision detection
  (when (:platform? e) (rectangle! (:hit-box e) :contains x y)))

(defn on-platform? [entities x y]
  (some #(platform-test % x y) entities))

(defn move-floating-platform 
  "Doesn't work yet - needs to transfer velocity to mobs"
  [{:keys [x floating-platform?] :as e}]
  (if floating-platform?
  (let [new-x (if (< x world/width) (+ x world/platform-speed) 0)]
    (assoc e :x new-x)
    )
    e
    ) 
  )
