%%%==========================================================
%%% ping pong hello remote example mirroring the scala actor
%%% example. http://erlang.org/doc/getting_started/conc_prog.html
%%% 1) create .erlang.cookie
%%% 2) this_is_very_secret
%%% 3) chmod 400 .erlang.cookie
%%% must be on both machines
%%%
%%% erl -name pong@192.168.100.22
%%% c(remote_pong).
%%% remote_pong:start_pong().
%%% erl -name ping@local_ip_address
%%% c(remote_pong).
%%% remote_pong:start_ping({'pong@192.168.100.22'}).
%%% remote_pong:start_ping({'pong@192.168.100.22', 5}).
%%%==========================================================
-module(remote_pong).
-export([
    start_ping/1,
    start_pong/0,
    ping/2,
    pong/0]).

ping(0, Pong_Node) ->
    {pong, Pong_Node} ! finished,
    io:format("ping finished~n", []);

ping(N, Pong_Node) ->
    {pong, Pong_Node} ! {ping, self()},
    receive
        pong ->
            io:format("Ping received pong~n", [])
    end,
    ping(N - 1, Pong_Node).

pong() ->
    receive
        finished ->
            io:format("Pong finished~n", []);
        {ping, Ping_PID} ->
            io:format("Pong received ping~n", []),
            Ping_PID ! pong,
            pong()
    end.

start_pong() ->
    register(pong, spawn(remote_pong, pong, [])).

start_ping({Pong_Node}) ->
    spawn(remote_pong, ping, [3, Pong_Node]);
start_ping({Pong_Node, N}) ->
    spawn(remote_pong, ping, [N, Pong_Node]).



