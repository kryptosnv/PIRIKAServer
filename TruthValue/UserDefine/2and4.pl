% This is created by katsura
% 2014/01/18 18:15:47
truth_width(1).

truth_value([X]).

top([X]).

bot([X]).

 % Definition of the magnitude relation.
ge([X],[X]) :- !.

lub([X],[X],[X]) :- !.
