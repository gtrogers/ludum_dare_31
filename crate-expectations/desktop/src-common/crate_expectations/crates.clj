(ns crate-expectations.crates
  (:require [play-clj.math :refer [rectangle]]
            [play-clj.g2d :refer [animation->texture animation texture play-mode]]
            [play-clj.core :refer [add-timer!]]
            [crate-expectations.mobs :as mobs]
            [crate-expectations.world :as world]
            ))

(defn crate-data [x y tag]
  ;; TODO better organise entitiy creation
  (merge (mobs/mob-data x y tag) {:crate? true
                                  :platform? true
                                  :hit-box (rectangle x (+ y 56) 64 world/pixels-per-move)
                                  :blink (animation 0.15 [(texture "crate_test.png") (texture "crate_test_flash.png")] :set-play-mode  (play-mode :loop-pingpong))
                                  }) 
  )

(defn open-crates! [screen {:keys [crate? on-floor? blink] :as e}]
  (if (and crate? on-floor?) 
    (merge e (animation->texture screen blink))
    e
    )
  )
