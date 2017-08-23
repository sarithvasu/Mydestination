package mudio.sumanth.come.mydestination;

import java.util.List;

import mudio.sumanth.come.mydestination.Route;


/**
 * Created by sarith.vasu on 20-01-2017.
 */

public interface RoutingListener {
    void onRoutingFailure(RouteException e);

    void onRoutingStart();

    void onRoutingSuccess(List<Route> route, int shortestRouteIndex);

    void onRoutingCancelled();
}
