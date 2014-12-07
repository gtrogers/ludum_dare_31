(ns crate-expectations.enemies
  (:require [play-clj.g2d :refer [texture]]
            [play-clj.math :refer [rectangle]]
            [play-clj.core :refer [find-first]]
            [crate-expectations.mobs :as mobs]
            [crate-expectations.world :as world]
            ))

(def ^:private enemy-types {
                            :test {:hp 3
                                   :width 64
                                   :height 64
                                   :texture "test_enemy.png"}
                            })

(defn enemy [x y enemy-type]
  (let [template (enemy-type enemy-types)]
    (-> (texture (:texture template)) 
        (merge (mobs/mob-data x y :spawned-enemy))
        (assoc :hit-box (rectangle x y (:width template) (:height template)) :enemy? true :hp (:hp template))
        )))

(defn logic [player {:keys [enemy? x y x-velocity y-velocity on-floor?] :as e}]
  (let [player-x (:x player)
        player-y (:y player)] 
    (if enemy?
      (let [new-x-velocity (cond
                             (> x player-x) -4 
                             (< x player-x) 4 
                             :default x-velocity) 
            new-y-velocity (cond 
                             (and (< y player-y) on-floor?) 24
                             :default y-velocity)]
        (-> e
            (assoc :x-velocity new-x-velocity)
            (assoc :y-velocity new-y-velocity)))
      e)))

;; TODO all entities should have the same method of updating them

(defn remove-if-dead! [enemy]
  (when-not (< (:hp enemy) 0) enemy)
  )

(defn bullet-collisions [entities {:keys [enemy?] :as e}]
  (if enemy?
    (let [bullet (find-first (fn [{:keys [bullet? hit-box] :as bullet}]
                            (when (and bullet? hit-box) (world/overlap? (:hit-box e) hit-box))
                            ) entities)]
      (if bullet
        (-> e
            (assoc :hp (dec (:hp e)))
            (assoc :x-velocity (* world/knockback-factor (:x-velocity bullet)))
            ;;(assoc :y-velocity (* world/knockback-factor (:y-velocity bullet)))
            remove-if-dead!) 
        e)
      )
    e
    ) 
  )

