truth_width(1).
truth_value([t2]).
truth_value([f2]).
truth_value([top]).
truth_value([t4]).
truth_value([f4]).
truth_value([bot]).

top([top]).
bot([bot]).

ge([X],[X]) :- truth_value([X]),!.
ge([top],[X]) :- truth_value([X]),!.
ge([t2],[t4]) :- !.
ge([t2],[f4]) :- !.
ge([t4],[f2]) :- !.
ge([f4],[f2]) :- !.
ge([X], [bot]) :- truth_value([X]),!.

lub([X],[X],[X]) :- !.
lub([top],[X],[top]) :- truth_value([X]),!.
lub([X],[top],[top]) :- truth_value([X]),!.
lub([bot],[X],[X]) :- truth_value([X]),!.
lub([X],[bot],[X]) :- truth_value([X]),!.

lub([t4],[f4],[top]) :- !.
lub([f4],[t4],[top]) :- !.
