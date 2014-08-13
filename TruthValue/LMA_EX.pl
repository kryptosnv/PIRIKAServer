%%LMA.pl(Prol Eddition)

% 記号の定義
:- op(1150, xfx, '<==').		% ここでの implication
:- op(1000, xfy, '&').		% and 演算子
:- op(300, xfx, '::').		% リテラルに注釈を入れる記号 p(a)::t など
%:- op(910, fx, [not]).
:- op(450, fx, '~').
%:- op(901, xfx, '~').

%必要な定義を追加
append([], Z, Z).
append([W|X1], Y, [W|Z1]) :- append(X1, Y, Z1).

member(X, [X|_]).
member(X, [_|L]) :- member(X, L).

select(X, [X | Xs],Xs).
select(X,[Y | Ys],[Y | Zs]) :- select(X,Ys,Zs).

not(P) :- P, !, fail.
not(_).

% 部分集合の定義.
subset([], _).
subset([Y|Ys], X) :- select(Y, X, Z),subset(Ys,Z).

setof(Term, Goal, List) :- findall(Term, Goal, List).

% suffix を定義
suffix(X, Y) :- append(_, X, Y).

read_Truth(FileName) :- [FileName].

%-----------------------------------------------------------
%------------------------------------------------------------------------------
% 攻撃関係
%------------------------------------------------------------------------------


negated_atom(~(_)). 

default_literal(not(_)). 


%論証の結論
concl(Head, Arg) :-
	member((Head <== _), Arg).

%論証の仮定
assm(Assm, Arg) :-
	member((_ <== Body), Arg),
	assm_in_body(Assm, Body).

assm_in_body(Assm, (Assm & _)) :- 
	default_literal(Assm). 

assm_in_body(Assm, (_ & Tail)) :- 
	assm_in_body(Assm, Tail).
 
assm_in_body(Assm, Assm) :- 
	default_literal(Assm).
		

%反論
rebut(Arg1, Arg2) :-
	concl(Head::Ann1, Arg1),
	concl(~Head::Ann2, Arg2),
	ge(Ann1, Ann2).

rebut(Arg1, Arg2) :-
	concl(~Head::Ann1, Arg1),
	concl(Head::Ann2, Arg2),
	ge(Ann2, Ann1).


%無効化
undercut(Arg1, Arg2) :-
	concl(Head::Ann1, Arg1),
	assm(not Head::Ann2, Arg2),
	ge(Ann1, Ann2).	

undercut(Arg1, Arg2) :-
	concl(~Head::Ann1, Arg1),
	assm(not ~Head::Ann2, Arg2),
	ge(Ann2, Ann1).	

%完全な無効化
st_undercut(Arg1, Arg2) :-
	undercut(Arg1, Arg2),
	\+ undercut(Arg2, Arg1).



%論破
defeat(Arg1, Arg2) :-
	undercut(Arg1, Arg2).

defeat(Arg1, Arg2) :-
	rebut(Arg1, Arg2),
	\+ undercut(Arg2, Arg1).


%無効化する論証を探す
find_undercutter(Undercutter, Arg) :- 
	assm(not ~Head::Ann2, Arg),
	k((~Head::Ann1  <== _)),
	ge(Ann2, Ann1),
	query(~Head::Ann1, Undercutter).

find_undercutter(Undercutter, Arg) :- 
	assm(not Head::Ann2, Arg),
	k((Head::Ann1  <== _)),
	ge(Ann1, Ann2),
	query(Head::Ann1, Undercutter).

%完全に無効化する論証を探す
find_st_undercutter(St_undercutter, Arg) :- 
	find_undercutter(St_undercutter, Arg),
	\+ undercut(Arg, St_undercutter).


%論証を書く
write_args_u([A|B]):-
	write(A),write('.'),
	nl,
	write_args_u(B).

write_args_u([]):-
	!.

%st_undercutterをファイルに出力する
write_st_undercutter(Args,Filename):-
	setof(D,find_st_undercutter(D,Args),List),	
	tell(Filename),
	write_args_u(List),
	told;
	tell(Filename),
	told.

%st_undercutterをファイルに出力しない場合
write_st_undercutter_2(Args, List) :-
        setof(D,find_st_undercutter(D,Args),List).

%反論する論証を探す
find_rebutter(Rebutter, Arg) :- 
	concl(~Head::Ann2, Arg),
	k((Head::Ann1  <== _)),
	ge(Ann1, Ann2),
	query(Head::Ann1, Rebutter).

find_rebutter(Rebutter, Arg) :- 
	concl(Head::Ann2, Arg),
	k((~Head::Ann1  <== _)),
	ge(Ann2, Ann1),
	query(~Head::Ann1, Rebutter).



%論破する論証を探す
find_defeatter(Defeatter, Arg) :- 
	find_undercutter(Defeatter, Arg).
	
find_defeatter(Defeatter, Arg) :- 
	find_rebutter(Defeatter, Arg),
	\+ undercut(Arg, Defeatter).



%論証を書く
write_args_d([A|B]):-
	write(A),write('.'),
	nl,
	write_args_d(B).

write_args_d([]):-
	!.

%defeatterをファイルに出力する
write_defeatter(Args,Filename):-
	setof(D,find_defeatter(D,Args),List),	
	tell(Filename),
	write_args_d(List),
	told;
	tell(Filename),
	told.

%defeatterのファイル出力しない場合
write_defeatter_2(Args, List) :-
        setof(D, find_defeatter(D, Args), List).


%抽象議論フレームワークを使うための定義
x_attack(Arg1, Arg2) :- defeat(Arg1, Arg2).
y_attack(Arg1, Arg2) :- st_undercut(Arg1, Arg2).


find_x_attacker(X_attacker, Arg) :- find_defeatter(X_attacker, Arg).
find_y_attacker(Y_attacker, Arg) :- find_st_undercutter(Y_attacker, Arg).


%------------------------------------------------------------------------------
% すべての論証を求める
%------------------------------------------------------------------------------


argument_set(Args) :-
        setof(Head, Body^k((Head  <== Body)), Heads), %ヘッドを収集する
	setof(Arg, X^(member(X, Heads), query(X, Arg)), Args).


%------------------------------------------------------------------------------
%EALPの集合から論証を作る
%------------------------------------------------------------------------------

args_mas(Args, MasFileNAme) :-
	consult(MasFileNAme),
	multi_agent_system(MAS),
	make_arguments(Args, MAS),!.
		

make_arguments(Args3, [KB | KBs]) :-
	read_file(KB),
	argument_set(Args1),
	make_arguments(Args2, KBs),
	append(Args1, Args2, Args3).
	
make_arguments([], []).



%------------------------------------------------------------------------------
%ファイルの読み込み
%------------------------------------------------------------------------------

read_file(FileName) :-
	retractall(k),
	see(FileName),
	tell(temp),
	%open(temp, write, _),
	read_file,
	told,
	seen,
	consult(temp).

read_file(FileName, TempFile) :-
	retractall(k),
	see(FileName),
	tell(TempFile),
	%open(temp, write, _),
	read_file,
	told,
	seen,
	consult(TempFile).
	
%EALP用の読み込みを追加
%kをつけた述語を動的に読み込む
read_EALP(FileName) :- 
        see(FileName), %ファイル入力
        read_EALP,
        seen.

%再帰的にファイルを読み込み
read_EALP :- 
        read(Clause),
        read_EALP_1(Clause).
        
%ファイルの最後に到達した
read_EALP_1(end_of_file).

%1行読み込み
read_EALP_1(Clause) :-
        assert(k(Clause)),
        read_EALP.

%------------------------------------------------------------------------------
% 読み込んだ clause を この program で扱う形式に変換
%------------------------------------------------------------------------------

read_file :-
	read(Clause),
	end_or_clause(Clause).

end_or_clause(end_of_file).
end_or_clause(Clause) :-
	write_clause(Clause),
	read_file.

write_clause(Clause) :-
	write('k(('),
	write(Clause),
	write(')).'),
	nl.




%-----------------------------------------------------------
% GAPに質問する．
%-----------------------------------------------------------


query(Head::X,Trace) :-
	reductant((Head::Y <== Body)),
	ge(Y,X),
	query(Body,Tracetemp),
	append([(Head::Y <== Body)],Tracetemp,Trace).

query(~Head::X,Trace) :-
	k((~Head::Y <== Body)),
	ge(X,Y),
	query(Body,Tracetemp),
	append([(~Head::Y <== Body)],Tracetemp,Trace).

query((Initial & Tail),Trace) :-	
	query(Initial,Temp1),
	query(Tail,Temp2),
	append(Temp1,Temp2,Trace).

query(not(_),[]).
     
query(true,[]).

%論証を書く
write_args_q([A|B]):-
	write(A),write('.'),
	nl,
	write_args_q(B).

write_args_q([]):-
	!.	

%本論をファイルに出力する
write_query(Head::X, Filename):-
	setof(Trace,query(Head::X,Trace),List),	
	tell(Filename),
	write_args_q(List),
	told;
	tell(Filename),
	told.

write_query(~Head::X, Filename):-
	setof(Trace,query(~Head::X,Trace),List),	
	tell(Filename),
	write_args_q(List),
	told;
	tell(Filename),
	told.

write_query(_, Filename) :- tell(Filename),told.

%ファイル書き込みをしない(write_query)
write_query_2(Head::X, List):- 
        setof(Trace,query(Head::X,Trace),List).

write_query_2(~Head::X, List):-
        setof(Trace,query(~Head::X,Trace),List).

write_query_2(_, []).


%-----------------------------------------------------------
% 極小還元を求める．
%　reductant(R) Rは極小還元である．
%
% k((q::t <== r::t)).
% k((q::f <== p::f)).
%
% | ?- reductant(X).         
% X = q::top<==p::f&r::t ? ;
% X = q::f<==p::f ? ;
% X = q::t<==r::t ? ;
% no
%-----------------------------------------------------------

reductant((Head::X <== Body)) :-
	setof((Head::Y <== Z), k((Head::Y <== Z)), Set),	
	truth_width(N),
	ncr2(N, Set, Sub),
	reductant(Sub, (Head::X <== Body)).



%reductant(List, R)　規則のリスト Listから作られる極小還元をRと単一化

reductant([(H::X1 <== B1) | List], (H::X3 <== B1&B2)) :-
	reductant(List, (H::X2 <== B2)),
	\+ ge(X1, X2),
	\+ ge(X2, X1),
	lub(X1,X2,X3).

reductant([(H::X <== B)], (H::X <== B)).


	 


%-----------------------------------------------------------
%組み合わせを求める
% ncr(N,List,R). Listの中からN個の要素を取り出した結果をRと単一化．
%
% | ?- ncr(2,[a,b,c,d],R).    
% R = [a,b] ? ;
% R = [a,c] ? ;
% R = [a,d] ? ;
% R = [b,c] ? ;
% R = [b,d] ? ;
% R = [c,d] ? ;
% no
%-----------------------------------------------------------

ncr(N, List, Result) :-
	N > 0,
	suffix([X|Y], List),
	Count is N-1,
	ncr(Count, Y, Temp),
	append([X], Temp, Result).

ncr(0, _, []).


%-----------------------------------------------------------
%組み合わせを求める　其の2
% ncr2(N,List,R). Listの中から最大N個の要素を取り出した結果をRと単一化．
%
% | ?- ncr2(2,[a,b,c,d],R).                  
% R = [a,b] ? ;
% R = [a,c] ? ;
% R = [a,d] ? ;
% R = [b,c] ? ;
% R = [b,d] ? ;
% R = [c,d] ? ;
% R = [a] ? ;
% R = [b] ? ;
% R = [c] ? ;
% R = [d] ? ;
% no
%-----------------------------------------------------------

ncr2(M, List, Result) :-
	M > 0,
	ncr(M, List, Result).

ncr2(M,List, Result) :-
	M > 0,
	Count is M-1,
	ncr2(Count, List, Result).



