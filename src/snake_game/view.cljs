(ns snake-game.view
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [reagent.core :as reagent :refer [atom]]
            [re-frame.core :refer [subscribe dispatch]]))

(defn render-brett
  "Renders the game brett area with the slange and the skatt to catch"
  []
  (let [brett (subscribe [:brett])
        slange (subscribe [:slange])
        hindre (subscribe [:hindre])]
    (fn []
      (let [[width height] @brett
            slange-positions (into #{} @slange)
            hindre @hindre
            cells (for [y (range height)]
                    (into [:tr]
                          (for [x (range width)
                                :let [current-pos [x y]]]
                            (cond
                              (slange-positions current-pos) [:td.slange-on-cell]
                              (hindre current-pos) [:td.skatt]
                              :else [:td.cell]))))]
        (into [:table.stage {:style {:height 377
                                     :width 527}}]
              cells)))))

(defn game-over
  "Renders the game over overlay if the game is finished"
  []
  (let [game-state (subscribe [:er-spillet-igang?])]
    (fn []
      (if @game-state
        [:div]
        [:div.overlay
         [:div.play {:on-click #(dispatch [:initialize])}
          [:h1 "↺" ]]]))))

(defn poeng
  "Renders player's poeng"
  []
  (let [poeng (subscribe [:poeng])]
    (fn []
      [:div.poeng (str "Poeng: " @poeng)])))

(defn game
  "The main rendering function"
  []
  [:div
   [game-over]
#_   [poeng]
   [render-brett]])
