(ns crate-expectations.enemies
  (:require [play-clj.g2d :refer [texture]]
            [crate-expectations.mobs :as mobs]))

(def ^:private enemy-types {
                            :test {:width 64 :height 64 :texture "test_enemy.png"}
                            })

(defn enemy [x y enemy-type]
  (let [template (enemy-type enemy-types)]
    (-> (texture (:texture template)) 
        (merge (mobs/mob-data x y :spawned-enemy))
        (assoc :width (:width template) :height (:height template))
        )
    )
  )

