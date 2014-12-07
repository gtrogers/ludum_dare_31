(ns crate-expectations.crates
  (:require [play-clj.math :refer [rectangle]]
            [play-clj.g2d :refer [animation->texture animation texture play-mode]]
            [play-clj.core :refer [add-timer!]]
            [crate-expectations.mobs :as mobs]
            [crate-expectations.world :as world]
            )
  (:import [com.badlogic.gdx.utils TimeUtils])
  )

(defn crate-data [x y tag]
  ;; TODO better organise entitiy creation
  (merge (mobs/mob-data x y tag) {:crate? true
                                  :platform? true
                                  :hit-box (rectangle x (+ y 50) 36 world/pixels-per-move)
                                  :hit-box-offsets {:x 0 :y 50}
                                  :blink (animation 0.15 [(texture "crate.png") (texture "crate_flashing.png")] :set-play-mode  (play-mode :loop-pingpong))
                                  }) 
  )

(defn flash-crate! [screen {:keys [crate? on-floor? blink armed] :as e}]
  (if (and crate? on-floor?) 
    (merge e (animation->texture screen blink))
    e))

(defn arm-crate! [{:keys [armed on-floor? crate?] :as e}]
  (if (and crate? on-floor? (not armed))
    (assoc e :armed (TimeUtils/millis))
    e
    )
  )

(defn open-crate! [{:keys [armed] :as e}] 
  (if-let [armed-time armed] 
    (let [time-since-armed (- (TimeUtils/millis) armed-time)]
      (if (> time-since-armed 2500)
        (assoc e :spawn-and-destroy! true) 
        e)) 
    e))

(defn spawn [entities]
  (let [num-crates (count (filter :crate? entities))]
    (if (< num-crates 5) (conj entities (merge (texture "crate.png")
                                               (crate-data (rand 310) 400 :spawned-crate)))
      entities)))
