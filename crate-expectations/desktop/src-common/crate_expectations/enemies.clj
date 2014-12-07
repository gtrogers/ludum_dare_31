(ns crate-expectations.enemies
  (:require [play-clj.g2d :refer [texture texture! animation play-mode animation->texture]]
            [play-clj.math :refer [rectangle]]
            [play-clj.core :refer [find-first]]
            [crate-expectations.mobs :as mobs]
            [crate-expectations.world :as world]
            ))

(def ^:private enemy-types {
                            :test {:hp 3
                                   :width 28
                                   :height 48
                                   :texture "snowmen.png"}
                            })

(defn animate-enemy [screen {:keys [enemy? anim] :as e}]
  (if enemy? 
    (merge e (animation->texture screen anim)) 
    e)
  )

(defn enemy [x y enemy-type]
  (let [template (enemy-type enemy-types)
        tex (texture! (texture (:texture template)) :split 32 64)
        sprites (map (fn [i] (texture (aget tex 0 i))) [0 1])
        anim (animation 0.3 sprites :set-play-mode (play-mode :loop-pingpong))
        ]
    (-> (first sprites) 
        (merge (mobs/mob-data x y :spawned-enemy))
        (assoc :hit-box (rectangle x y (:width template) (:height template))
               :hit-box-offsets {:x 2 :y 0}
               :enemy? true
               :hp (:hp template)
               :anim anim
               )
        )))

(defn logic [player screen {:keys [enemy? x y x-velocity y-velocity on-floor?] :as e}]
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
        (-> (animate-enemy screen e) 
            (assoc :x-velocity new-x-velocity)
            (assoc :y-velocity new-y-velocity)))
      e)))

;; TODO all entities should have the same method of updating them

(defn remove-if-dead! [enemy]
  (if-not (< (:hp enemy) 0)
    enemy
    (do (swap! world/score inc) 
        nil) 
    )
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
            remove-if-dead!) 
        e)
      )
    e
    ) 
  )

