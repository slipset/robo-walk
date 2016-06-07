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
                             utfør-flytt
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
(register-handler :endre-retning endre-retning)

;;Register global event listener for keydown event.
;;Processes key strokes according to `utils/key-code->move` mapping
(defonce key-handler
  (events/listen js/window "keydown"
                 (fn [e]
                   (let [key-code (.-keyCode e)]
                     (when (finnes-i? piltast-til-retning key-code)
                       (dispatch [:endre-retning (piltast-til-retning key-code)]))))))

;; ---- Subscription Handlers ----

(register-sub
 :brett
 (fn
   [db _]                         ;; db is the app-db atom
   (reaction (:brett @db))))      ;; wrap the computation in a reaction

(register-sub
 :slange
 (fn
   [db _]
   (reaction (:kropp (:slange @db)))))

(register-sub
 :skatt
 (fn
   [db _]
   (reaction (:skatt @db))))

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
