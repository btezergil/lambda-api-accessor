(ns com.webhook.core
  (:gen-class)
  (:require [fierycod.holy-lambda.response :as hr]
            [fierycod.holy-lambda.agent :as agent]
            [fierycod.holy-lambda.core :as h]
            [clojure.tools.logging :as log]
            [cheshire.core :as cheshire]))

(defn execute-bot-actions
      "Executes the associated action with Telegram bot commands"
      [bot-command]

      (condp = bot-command
             "/mokoko" (log/info "come on, mokoko?")
             "/testmessage" (log/info "message from webhook")
             (log/info (format "no action defined for command %s " bot-command))))

(defn webhook-handler
      "Handles webhook updates"
      [{:keys [message]}]
      (let [{command :text
             entities :entities} message
            bot-command (= "bot_command" (-> entities
                                             first
                                             :type))]
            (when bot-command
              (do (log/info (format "received a bot command: %s " command))
                  (execute-bot-actions command)))))

(defn TelegramWebhookLambda
  "Main clojure native Lambda function"
  [{:keys [event ctx] :as request}]

  (log/info (format "request: %s" request))

  ;; parse body: (cheshire/parse-string (:body request) true)
  (let [body (-> request
                 :event
                 :body-parsed)]
    (hr/text (webhook-handler body)))

  ;; return a successful plain text response. See also, hr/json
  )

;; Specify the Lambda's entry point as a static main function when generating a class file
(h/entrypoint [#'TelegramWebhookLambda])

;; Executes the body in a safe agent context for native configuration generation.
;; Useful when it's hard for agent payloads to cover all logic branches.
(agent/in-context
 (println "I will help in generation of native-configurations"))
