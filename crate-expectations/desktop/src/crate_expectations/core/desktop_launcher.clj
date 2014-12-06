(ns crate-expectations.core.desktop-launcher
  (:require [crate-expectations.core :refer :all])
  (:import [com.badlogic.gdx.backends.lwjgl LwjglApplication]
           [org.lwjgl.input Keyboard])
  (:gen-class))

(defn -main
  []
  (LwjglApplication. crate-expectations "crate-expectations" 400 300)
  (Keyboard/enableRepeatEvents true))
