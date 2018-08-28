%% So this is a kitty server/store. The behavior is extremely simple:
%% you describe a cat and you get that cat. If someone returns a cat,
%% it's added to a list and is then automatically sent as the next order
%% instead of what the client actually asked for (we're in this kitty store for
%% the money, not smiles)
-module(kitty_server).
-export([start_link/0, order_cat/4, return_cat/2, close_shop/1]).
%my_server will need these to be able to make the calls
-export([init/1, handle_call/3, handle_cast/2]).

-record(cat, {name, color=green, description}).

%?MODULE is the same as passing kitty_server
start_link() -> my_server:start(?MODULE, []).

order_cat(Pid, Name, Color, Description) ->
    my_server:call(Pid, {order, Name, Color, Description}).

return_cat(Pid, Cat = #cat{}) ->
    my_server:cast(Pid, {return, Cat}).

close_shop(Pid) ->
    my_server:call(Pid, terminate).

%Server Functions
init([]) -> [].

handle_call({order, Name, Color, Description}, From, Cats) ->
    if Cats =:= [] ->
            my_server:reply(From, make_cat(Name, Color, Description)),
            Cats;
       Cats =/= [] ->
            my_server:reply(From, hd(Cats)),
            tl(Cats)
    end;
handle_call(terminate, From, Cats) ->
    my_server:reply(From, ok),
    terminate(Cats).

handle_cast({return, Cat = #cat{}}, Cats) ->
    [Cat|Cats].

%private functions
make_cat(Name, Col, Desc) ->
    #cat{name=Name, color=Col, description=Desc}.

%log and then kill the server
terminate(Cats) ->
    [io:format("~p was set free.~n",[C#cat.name]) || C <- Cats],
    exit(normal).

