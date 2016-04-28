(ns snake-game.core
  (:require
   [reagent.core :as reagent :refer [atom]]
   [re-frame.core :refer [dispatch dispatch-sync]]
   [snake-game.handlers :as handlers]
   [snake-game.view :as view]))

;;Dispatch next state event every 150ms
(defonce snake-moving
  (js/setInterval #(dispatch [:oppdater-spill]) 250))

(defn run
  "Main app function"
  []
  (dispatch-sync [:initialize])
  (reagent/render [view/game]
                  (js/document.getElementById "app")))

(run)
