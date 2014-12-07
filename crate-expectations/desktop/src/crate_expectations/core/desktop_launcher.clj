(ns crate-expectations.core.desktop-launcher
  (:require [crate-expectations.core :refer :all])
  (:import [com.badlogic.gdx.backends.lwjgl LwjglApplication LwjglApplicationConfiguration]
           [org.lwjgl.input Keyboard])
  (:gen-class))

(defn -main
  []
  (Keyboard/enableRepeatEvents true)
  (let [config (LwjglApplicationConfiguration.)]
    (doto config
      (-> .width (set! 800))
      (-> .height (set! 600))
      (-> .title (set! "Crate Expectations"))
      (-> .fullscreen (set! false)) ;; TODO how to do nice cross platform fullscreen - look into view ports
      (-> .resizable (set! false))
      )
    (LwjglApplication. crate-expectations config)) 
  )
