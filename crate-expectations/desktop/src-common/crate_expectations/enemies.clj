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
        (assoc :width (:width template) :height (:height template) :enemy? true)
        )
    )
  )

(defn logic [player {:keys [enemy? x y x-velocity y-velocity on-floor?] :as e}]
  (let [player-x (:x player)
        player-y (:y player)] 
    (if enemy?
      (let [new-x-velocity (cond
                             (> x player-x) -4 
                             (< x player-x) 4 
                             :default x-velocity) 
            new-y-velocity (cond 
                             (and (< y player-y) on-floor?) 16
                             :default y-velocity)]
        (-> e
            (assoc :x-velocity new-x-velocity)
            (assoc :y-velocity new-y-velocity)))
      e)) 
  )
