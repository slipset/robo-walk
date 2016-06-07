(ns snake-game.core
  (:require
   [reagent.core :as reagent :refer [atom]]
   [re-frame.core :refer [dispatch dispatch-sync]]
   [snake-game.handlers :as handlers]
   [snake-game.view :as view]))

(defonce snake-moving
  (js/setInterval #(dispatch [:oppdater-spill] ) 750))

(defn run "Main app function" []
  (dispatch-sync [:initialize])
  (reagent/render [view/game]
                  (js/document.getElementById "app")))

(run)
(comment
  (do (use 'figwheel-sidecar.repl-api) (start-figwheel!) (cljs-repl))
)
