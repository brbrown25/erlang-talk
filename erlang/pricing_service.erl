% Basic Example of roll your own hot code loading.
% inspired by https://github.com/nickmcdowall/Erlang-Examples/wiki/Hot-Code-Swapping.
% Basic Pricing Service where, a user requests an item and it's price is returned.
% Usage:
%   c('erlang/pricing_service').
%   Service = pricing_service:link_with(self()).
%   Service ! version.
%   Service ! {price, milk}.
%   Service ! {price, granola}. --fails
%  clear and reload
%  change version, add item, reload, run.
-module(pricing_service).
-export([link_with/1, message_receiver/0, handle/1]).
-define(VERSION, '1.0.0').

link_with(ClientPid) ->
    register(client, ClientPid),
    spawn_link(?MODULE, message_receiver, []).

message_receiver() ->
    receive Request -> ?MODULE:handle(Request),
                       ?MODULE:message_receiver()
    end.

handle(Request) ->
    case Request of
        version -> reply_with_version();
        {price, Item} -> reply_with_price_of(Item)
    end.

reply_with_version() ->
    client ! io:format("Service Version:[~p]~n",[?VERSION]).

reply_with_price_of(Item) ->
    client ! io:format("The price of ~p is: $~p ~n", [Item, price(Item)]).

price(Item) ->
    case Item of
        tea -> 2.05;
        coffee -> 2.10;
        milk -> 0.99;
        bread -> 0.50
        % bread -> 0.50;
        % granola -> 3.00
    end.

