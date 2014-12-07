(ns crate-expectations.world)

(def ^:const bullet-speed 3)
(def ^:const pixels-per-move 8)
(def ^:const gravity -2.5)
(def ^:const damping 0.3)
(def ^:const deceleration 0.9)
(def ^:const max-velocity 15)
(def ^:const jump-velocity (* 8 max-velocity))
(def ^:const height 300)
(def ^:const width 400)
(def ^:const base 28)

(defn off-screen [x y]
  (or (> 0 x)
      (< width x)
      (> base y)
      (< height y)
      )
  )
