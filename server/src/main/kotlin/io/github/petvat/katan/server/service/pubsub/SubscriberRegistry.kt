package io.github.petvat.katan.server.service.pubsub

import io.github.petvat.katan.server.service.client.ConnectedClient
import java.util.concurrent.ConcurrentHashMap

//class SubscriberRegistry {
//    private val subscriptions = ConcurrentHashMap<ConnectedClient, MutableSet<Subscription>>()
//
//    fun subscribe(client: ConnectedClient, subscription: Subscription) {
//        subscriptions.computeIfAbsent(client) { mutableSetOf() }.add(subscription)
//    }
//
//    fun unsubscribe(client: ConnectedClient, subscription: Subscription) {
//        subscriptions[client]?.remove(subscription)
//        if (subscriptions[client]?.isEmpty() == true) {
//            subscriptions.remove(client)
//        }
//    }
//
//    fun getSubscribers(type: SubscriptionType, id: RealmId? = null): Set<ConnectedClient> {
//        return subscriptions.entries
//            .filter { (_, subs) ->
//                subs.any {
//                    // TODO: Fix global scope logic
//                    it.type == type && (it.type.globalScope || it.realm == id)
//                }
//            }
//            .map { (userId, _) -> userId }
//            .toSet()
//    }
//
//}
