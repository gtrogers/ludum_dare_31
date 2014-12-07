(ns crate-expectations.mobs
  (:require [play-clj.core :refer [key-pressed? find-first]]
            [play-clj.math :refer [rectangle rectangle!]]
            [play-clj.g2d :refer :all]
            [crate-expectations.world :as world]
            [crate-expectations.platforms :as platforms])
  (:import [com.badlogic.gdx.utils TimeUtils])
  )

(defn- decelerate [v]
  (let [new-v (* world/deceleration v)]
    (if (< (Math/abs new-v) world/damping)
      0
      new-v)))

(defn mob-data [x y tag]
  (assoc {:angle 0
          :x x
          :y y 
          :x-velocity 0
          :y-velocity 0
          :on-floor? false
          :mob? true
          }
         tag true))

(defn player-data [x y tag]
  (assoc (mob-data x y tag) :hit-box (rectangle x y 16 32) :hp 5 :last-hit 0) 
  )

(defn player [x y tag screen]
  (let [tex (texture! (texture "player.png") :split 16 32) 
        sprites (map (fn [i] (texture (aget tex 0 i))) [0 1 2])
        right-animation (animation 0.15 sprites :set-play-mode (play-mode :loop-pingpong))
        left-animation (animation 0.15 (map #(texture % :flip true false) sprites) :set-play-mode (play-mode :loop-pingpong))
        ]
    (merge (animation->texture screen right-animation) 
     (assoc (player-data x y tag) :right-animation right-animation
            :left-animation left-animation
            )) 
    )
  )

(defn- clamp [n lower upper]
  (cond
    (> n upper) upper
    (< n lower) lower
    :default    n
    ))

(defn- get-x-velocity [tag e]
  (if (tag e)
    (cond
      (key-pressed? :a)  (* -1 world/max-velocity)
      (key-pressed? :d) world/max-velocity
      :default              (:x-velocity e)
      )
    (:x-velocity e)
    ))

(defn- get-y-velocity [tag {:keys [y-velocity on-floor?] :as e}] 
  (if (tag e)
    (cond
      (key-pressed? :w)   (if (and on-floor? (> 0 y-velocity)) world/jump-velocity y-velocity)
      :default             y-velocity 
      )
    (:y-velocity e)
    ))


(defn animate-player [screen {:keys [player? right-animation left-animation x-velocity] :as e}]
  (if player?
   (cond 
     (< 0 x-velocity) (merge e (animation->texture screen right-animation))
     (> 0 x-velocity) (merge e (animation->texture screen left-animation))
     :default         e
     ) 
    e)
  )

(defn move [{:keys [delta-time] :as screen} {:keys [x y] :as e}]
  (if (:mob? e)
    (let [x-velocity (get-x-velocity :player? e)
          y-velocity (+ (get-y-velocity :player? e) world/gravity) 
          delta-x (* delta-time x-velocity world/pixels-per-move)
          delta-y (* delta-time y-velocity world/pixels-per-move)]
      (assoc (animate-player screen e)
             :x (+ x delta-x)
             :y (+ y delta-y) 
             :x-velocity (decelerate x-velocity)
             :y-velocity (decelerate y-velocity) 
             :delta-x delta-x
             :delta-y delta-y
             ))
    e))

(defn clip [entities {:keys [x y delta-x delta-y] :as e}]
  (if (:mob? e)
    (let [old-x (- x delta-x)
          old-y (- y delta-y)
          on-platform? (platforms/on-platform? entities old-x old-y)
          new-y (clamp y world/base world/height)
          down? (> old-y new-y)
          used-y (if (and down? on-platform?) old-y new-y)
          used-x (clamp x 0 world/width)
          ]
      (assoc e
             :x used-x
             :y used-y 
             :on-floor? (or on-platform? (= used-y world/base))
             ))
    e
    ))

(defn- player-hit [{:keys [x y] :as enemy} {:keys [last-hit hp] :as player}]
  (if (> (- (TimeUtils/millis) last-hit) 1500)
    (-> player
        (assoc :x-velocity (if (> (:x player) x)
                             world/player-knockback
                             (* -1 world/player-knockback))) 
        (assoc :y-velocity (/ world/jump-velocity 2))
        (assoc :on-floor false)
        (assoc :hp (dec hp)) 
        (assoc :game-over? (when (< (:hp player) 0) true)) 
        (assoc :last-hit (TimeUtils/millis))) 
    player))

;; TODO make a player namespace?
(defn player-collisions [entities {:keys [player?] :as e}]
  (if player?
    (let [enemy (find-first (fn [{:keys [enemy? hit-box]}]
                              (when (and enemy? hit-box)
                                (world/overlap? hit-box (:hit-box e))
                                )
                              ) entities)]
      (if enemy
        (player-hit enemy e)
        e
        )
      )
    e
    )
  )
