package mudio.sumanth.come.mydestination;

import java.util.List;

import mudio.sumanth.come.mydestination.Route;
import mudio.sumanth.come.mydestination.RouteException;

/**
 * Created by sarith.vasu on 20-01-2017.
 */

public interface Parser {
    List<Route> parse() throws RouteException;
}
