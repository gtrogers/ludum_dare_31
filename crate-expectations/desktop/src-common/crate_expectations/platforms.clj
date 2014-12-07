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

(defn- crate->crate-collisions [c1 c2]
  (when-not (= (:crate-id c1) (:crate-id c2))
    (rectangle! (:hit-box c1) :overlaps (:hit-box c2))
    )
  )

(defn- regular-collisions [mob {:keys [hit-box y] :as platform}]
  (let [platform-height (rectangle! hit-box :get-height)
        platform-top (+ platform-height y -3)]
    (and (> (:y mob) 
            platform-top)
         (rectangle! hit-box :overlaps (:hit-box mob)))))

(defn- platform-test [mob e]
  (when (:platform? e)
    (if  (and (:crate? mob) (:crate? e)) 
      (crate->crate-collisions mob e) 
      (regular-collisions mob e))))

(defn on-platform? [entities e]
  (some #(platform-test e %) entities))

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
