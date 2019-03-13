# KNATIVE EVENTING DEMO

### WEBHOOK HANDLER

Webhook Handler receives incoming events from ISV and pushes them to a GCP topic. Downstream systems read from this topic

### Prerequisites

- Google Cloud account
- Knative Service
- Knative Eventing
- GKE / Minikube

### Steps to Run

**Set the default Google Cloud Project**

`gcloud config set project arv-project-233314
`


**Create a Google Cloud Topic**

`gcloud pubsub topics create raw_event`

**Create a Google Cloud Subscription**

`gcloud pubsub subscriptions create webhook-handler-subscription --topic raw_event`

**Build the Docker Image**

`docker build . -t arvindkasale08/webhook-handler`

**Push the Docker Image**

`docker push arvindkasale08/webhook-handler`

**Deploy the Service**

Update the _service.yaml_ with _PROJECT ID_, _TOPIC NAME_ and _SUBSCRIPTION_

Apply the service.yaml file

`
kubectl -n default apply -f service.yaml
`


`kubectl logs -l serving.knative.dev/service=webhook-handler -c user-container --since=10m`

