truth_width(2).
truth_value([top]).
truth_value([t4]).
truth_value([f4]).
truth_value([bot]).

top([top]).
bot([bot]).

ge([X],[X]) :- truth_value([X]),!.
ge([top],[X]) :- truth_value([X]),!.
ge([X],[bot]) :- truth_value([X]),!.

lub([X],[X],[X]) :- !.
lub([top],[X],[top]) :- truth_value([X]),!.
lub([X],[top],[top]) :- truth_value([X]),!.
lub([X],[bot],[X]) :- truth_value([X]),!.
lub([bot],[X],[X]) :- truth_value([X]),!.
lub([t4],[f4],[top]) :- !.
lub([f4],[t4],[top]) :- !.
