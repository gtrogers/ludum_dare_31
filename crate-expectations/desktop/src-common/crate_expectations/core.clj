(ns crate-expectations.core
  (:require [play-clj.core :refer :all]
            [play-clj.ui :refer :all]
            [crate-expectations.world :as world]
            [crate-expectations.mobs :as mobs]
            ))

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (update! screen :camera (orthographic):renderer (stage))
    [
     (merge (shape :filled :rect 0 0 16 32 :set-color (color :green))
            (mobs/mob-data 0 0 :player)
            ) 
     ])

  :on-render
  (fn [screen entities]
    (clear!)
    (->> 
      (map (fn [entity]
             (->> (mobs/move screen entity)
                  (mobs/clip))) entities) 
      (render! screen)) 
    )

  :on-key-down
  (fn [screen entities])
  
  :on-resize
  (fn [screen entities] (height! screen world/height)))

(defgame crate-expectations
  :on-create
  (fn [this]
    (set-screen! this main-screen)))
