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
      (log/info (format "executing action of bot command: %s " bot-command))

      (condp = bot-command
             "/mokoko" (log/info "come on, mokoko?")
             "/testmessage" (log/info "message from webhook")))

(defn webhook-handler
      "Handles webhook updates"
      [update]
      (let [message (-> update
                        :message)
            bot-command? (= "bot_command" (-> message
                                              :entities
                                              first
                                              :type))
            command (:text message)]
           (when bot-command?
                 (log/info (format "received a bot command: %s " command))
                 (execute-bot-actions command))))

(defn TelegramWebhookLambda
  "I can run on Java, Babashka or Native runtime..."
  [{:keys [event ctx] :as request}]

  (log/info (format "request: %s" request))

  ;; parse body: (cheshire/parse-string (:body request) true)
  (let [body (-> request
                 :event
                 :body-parsed)]
    (log/info (format "parsed body: %s" body))
    (hr/text (webhook-handler body)))

  ;; return a successful plain text response. See also, hr/json
  )

;; Specify the Lambda's entry point as a static main function when generating a class file
(h/entrypoint [#'TelegramWebhookLambda])

;; Executes the body in a safe agent context for native configuration generation.
;; Useful when it's hard for agent payloads to cover all logic branches.
(agent/in-context
 (println "I will help in generation of native-configurations"))
