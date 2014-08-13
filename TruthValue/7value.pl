
truth_width(2).
truth_value([fit]).
truth_value([it]).
truth_value([fi]).
truth_value([ft]).
truth_value([t]).
truth_value([i]).
truth_value([f]).
top([fit]).

ge([X],[X]) :- !.
ge([fit],[X]) :- !.
ge([it],[t]) :- !.
ge([it],[i]) :- !.
ge([fi],[i]) :- !.
ge([fi],[f]) :- !.
ge([ft],[t]) :- !.
ge([ft],[f]) :- !.

lub([X],[X],[X]) :- !.
lub([fit],[X],[fit]) :- !.
lub([X],[fit],[fit]) :- !.
lub([it],[fi],[fit]) :- !.
lub([it],[ft],[fit]) :- !.
lub([ft],[it],[fit]) :- !.
lub([ft],[fi],[fit]) :- !.
lub([fi],[it],[fit]) :- !.
lub([fi],[ft],[fit]) :- !.
lub([ft],[t],[ft]) :- !.
lub([ft],[f],[ft]) :- !.
lub([ft],[i],[fit]) :- !.
lub([t],[ft],[ft]) :- !.
lub([f],[ft],[ft]) :- !.
lub([i],[ft],[fit]) :- !.
lub([fi],[f],[fi]) :- !.
lub([fi],[i],[fi]) :- !.
lub([fi],[t],[fit]) :- !.
lub([f],[fi],[fi]) :- !.
lub([i],[fi],[fi]) :- !.
lub([t],[fi],[fit]) :- !.
lub([it],[t],[it]) :- !.
lub([it],[i],[it]) :- !.
lub([it],[f],[fit]) :- !.
lub([t],[it],[it]) :- !.
lub([i],[it],[it]) :- !.
lub([f],[it],[fit]) :- !.
lub([t],[f],[ft]) :- !.
lub([f],[t],[ft]) :- !.
lub([t],[i],[it]) :- !.
lub([i],[t],[it]) :- !.
lub([f],[i],[fi]) :- !.
lub([i],[f],[fi]) :- !.

