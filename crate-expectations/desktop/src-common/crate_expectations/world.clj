(ns crate-expectations.world
  (:require [play-clj.math :refer [rectangle!]])
  )

(def score (atom 0))

(def ^:const starting-health 5)
(def ^:const platform-speed 1)
(def ^:const bullet-speed 3)
(def ^:const knockback-factor 8)
(def ^:const pixels-per-move 8)
(def ^:const gravity -2.5)
(def ^:const damping 0.3)
(def ^:const deceleration 0.9)
(def ^:const max-velocity 15)
(def ^:const jump-velocity (* 8 max-velocity))
(def ^:const player-knockback (* 80))
(def ^:const height 300)
(def ^:const width 400)
(def ^:const base 28)

(def ^:const profanities ["Humbug!" "Gadzooks!" "Egads!"])

(defn off-screen [x y]
  (or (> 0 x)
      (< width x)
      (> base y)
      (< height y)
      )
  )

(defn overlap? [r1 r2]
  (rectangle! r1 :overlaps r2)
  )
