-module (pattern_match).
-export ([convert_length/1]).
% Atoms are lower case and can't have a value

convert_length({centimeter, X}) ->
  {inch, X / 2.54};
convert_length({inch, Y}) ->
  {centimeter, Y * 2.54}.
% convert_length({inch, Y}) ->
%   {centimeter, Y * 2.54};
% convert_length({millimeter, Y}) ->
%   {inch, Y / 25.4}.
