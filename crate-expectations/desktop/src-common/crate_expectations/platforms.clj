(ns crate-expectations.platforms
  (:require [ play-clj.math :refer [rectangle rectangle!]]))

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
