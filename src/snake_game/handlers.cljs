(ns snake-game.handlers
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :refer [register-handler
                                   register-sub
                                   dispatch]]
   [snake-game.norwegian :refer [finnes-i?]]
   [goog.events :as events]
   [snake-game.utils :refer [endre-retning
                             slange brett
                             er-det-en-kollisjon?
                             flytt-slangen
                             start-spill
                             oppdater-spill]]))

(def piltast-til-retning
  {38 [0 -1]
   40 [0 1]
   39 [1 0]
   37 [-1 0]})

(register-handler :initialize start-spill) 
(register-handler :oppdater-spill oppdater-spill)

;;Register global event listener for keydown event.
;;Processes key strokes according to `utils/key-code->move` mapping


;; ---- Subscription Handlers ----

(register-sub
 :brett
 (fn
   [db _]                         ;; db is the app-db atom
   (reaction (:brett @db))))      ;; wrap the computation in a reaction

(register-sub
 :hindre
 (fn [db _]
   (reaction (:hindre @db))))

(register-sub
 :slange
 (fn
   [db _]
   (reaction (:kropp (:slange @db)))))

(register-sub
 :poeng
 (fn
   [db _]
   (reaction (:poeng @db))))

(register-sub
 :er-spillet-igang?
 (fn
   [db _]
   (reaction (:er-spillet-igang? @db))))
