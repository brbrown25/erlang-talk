%%https://learnyousomeerlang.com/clients-and-servers
% c(kitty_gen_server).
% {ok, Pid} = kitty_gen_server:start_link().
% Pid ! <<"Test handle_info">>.
% Cat = kitty_gen_server:order_cat(Pid, "Cat Stevens", white, "not actually a cat").
% kitty_gen_server:return_cat(Pid, Cat).
% kitty_gen_server:order_cat(Pid, "Kitten Mittens", black, "look at them little paws!").
% kitty_gen_server:order_cat(Pid, "Kitten Mittens", black, "look at them little paws!").
% kitty_gen_server:return_cat(Pid, Cat).
% kitty_gen_server:close_shop(Pid).

-module(kitty_gen_server).
-behavior(gen_server).
-export([start_link/0, order_cat/4, return_cat/2, close_shop/1]).
-export([init/1, handle_call/3, handle_cast/2, handle_info/2,
         terminate/2, code_change/3]).

-record(cat, {name, color=green, description}).

%% Returns {ok, Pid}
start_link() -> gen_server:start_link(?MODULE, [], []).

%%Sync Call - call will timeout after 5 seconds by default
order_cat(Pid, Name, Color, Description) ->
    gen_server:call(Pid, {order, Name, Color, Description}).

%%Async Call
return_cat(Pid, Cat = #cat{}) ->
    gen_server:cast(Pid, {return, Cat}).

%%Sync Call
close_shop(Pid) ->
    gen_server:call(Pid, terminate).

%%Server Functions
init([]) -> {ok, []}.

handle_call({order, Name, Color, Description}, _From, Cats) ->
    if Cats =:= [] ->
            {reply, make_cat(Name, Color, Description), Cats};
       Cats =/= [] ->
            {reply, hd(Cats), tl(Cats)} %do example with that lower case to show compile warning
    end;
handle_call(terminate, _From, Cats) ->
    {stop, normal, ok, Cats}.

handle_cast({return, Cat = #cat{}}, Cats) ->
    {noreply, [Cat|Cats]}.

handle_info(Msg, Cats) ->
    io:format("Unexpected message: ~p~n",[Msg]),
    {noreply, Cats}.

terminate(normal, Cats) ->
    [io:format("~p was set free.~n",[C#cat.name]) || C <- Cats],
    ok.

code_change(_OldVsn, State, _Extra) ->
    %%this is where you handle code reloading which we will worry about later.
    {ok, State}.

%%Private functions
make_cat(Name, Col, Desc) ->
    #cat{name=Name, color=Col, description=Desc}.
