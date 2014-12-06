(ns crate-expectations.crates
  (:require [play-clj.math :refer [rectangle]]
            [crate-expectations.mobs :as mobs]
            [crate-expectations.world :as world]
            ))

(defn crate-data [x y tag]
  ;; TODO better organise entitiy creation
  (merge (mobs/mob-data x y tag) {:platform? true :hit-box (rectangle x (+ y 56) 64 world/pixels-per-move)}) 
  )
