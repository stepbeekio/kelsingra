# Kelsingra
Kelsingra is a tool to allow developers of multi-tenant systems to work against dev locally.

# What's the problem?

Complex, distributed systems are painful to work on. They require developers to maintain a great deal of state in while developing, and are difficult to test before deployment. There are many techniques to help enable safer deployments in a large distributed system - feature flags, tracing, additional test environments, canary deployments etc. - but they all fundamentally approach the problem from the perspective of deployed software.

What if we could instead work with a local copy of a subset of the system in a way that empowered developers with significantly tighter feedback loops? Kelsingra is intended as a PoC to solve that problem.

It's composed of an agent that runs in server mode in the dev environment, and in client mode on a developers machine. It requires some platform work to ensure that traffic is routed appropriately.

# What's in a name?

"Kelsingra" is a fictional city in the fantasy series "The Realm of the Elderlings" by Robin Hobb. It's a city where centuries ago the residents used fantastical technology in their daily lives. 

Kelsingra is a reference to what's been lost in recent development trends. Being able to developer a system locally is a hugely important part of developer experience and giving it up in favour of multiple services leads to us losing the "old magics".

# How does it work?

Kelsingra is intended for use in multi-tenant systems. This is because we need a way to route traffic to locally running copies of an application/database without corrupting the overall state of the system. One could use kelsingra without this split by tenant, but the result of this is almost certainly a lack of consistency. 

### X-Tenant-ID

The first requirement is a header to allow us to route traffic to kelsingra's agent in the dev environment. As an example, in a kubernetes deployment that relies on traefik as a reverse proxy, we would redeploy the kelsingra agent with updated kubernetes annotations to match on some header regexp that includes the required tenant identifier. 

This will route any traffic in the system for a particular tenant to the kelsingra service. In the case that kelsingra doesn't have an active override for the request, it'll strip the header and forward on to the reverse proxy again. 

### Kafka

Kelsingra needs to be configured with a consumer group and topic for any topic that we'd like to intercept. On the client side, kelsingra needs to be configured with the local kafka topics to forward back to dev. 

We can utilize kafka headers as in the X-Tenant-ID section above to ensure that we're only passing around the appropriate messages.

### Distributed State

For persistence, Kelsingra on the server uses postgresql and kafka. Upon receiving a request, kelsingra will check postgresql for any active clients. If a client is active for a particular request then kelsingra will publish the message to a kafka topic created dynamically for that tenant with a topic name like "$kelsingra-$service-$tenant". A kelsingra instance that has an active connection for that service and tenant combination will consume those messages and forward to the agent.

### Client Server Connection

When connecting to kelsingra from the client instance, one uses the web ui that's running locally. Upon configuring the instance, kelsingra will open a websocket request to the server. This websocket connection will allow the kelsingra client to subscribe to multiple service/tenant combinations to empower the user to run a subset of the system locally.


## TODO:
* kafka consumer group auto-overrides
* local host tunnel
  * need to setup redirect to point to the kelsingra server
  * need to add a permissive cors policy
  * need to make request to other server from browser using CORs
  * 
* trace context propagation
* resttemplate header setting
* basic ui
