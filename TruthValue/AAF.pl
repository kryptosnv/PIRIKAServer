%抽象議論フレームワーク
%-----------------------------------------------------------

:- use_module(library(lists)).
%=======================================================================

%-----------------------------------------------------------
%　xy_Argumentation(Args, J, O, D).
%　抽象論証集合(リスト)Argsにおいて，
%　Jはx/y正当化された論証の集合であり，
%　Oはx/y却下された論証の集合であり．
%　Dはx/y防御可能な論証の集合である．


xy_argumentation(Args, J, O, D) :-
	xy_justified_args(Args,J), 
	xy_overruled_args(Args, J, O),
	xy_defensible_args(Args, J, O, D).




%=======================================================================

%-----------------------------------------------------------
%　xy_defensible_args(Args, J, O, D).
%　抽象論証集合(リスト)Argsにおいて，
%　Jをx/y正当化された論証の集合，Oをx/y却下された論証の集合としたとき，
%　Dはx/y防御可能な論証の集合である．

xy_defensible_args(Args, J, O, D) :- 
	append(J, O, JO),
	remove_list(Args, JO, D).


%-----------------------------------------------------------
%　remove_list(List, Remove, Result).
%　ListからRemoveのすべての要素を削除した結果がResultである．

remove_list(List, [X | Other], Result) :-
	delete(List, X, ListwithoutX),
	remove_list(ListwithoutX, Other, Result).

remove_list(List, [], List).
	



%=======================================================================

%-----------------------------------------------------------
%　xy_overruled_args(Args, J, O).
%　抽象論証集合(リスト)Argsにおいて，Jをx/y正当化された論証の集合としたとき，
%　Oはx/y却下された論証の集合である．

xy_overruled_args(Args, J, O) :- 
	findall(OArg, JArg^(member(JArg, J), y_attack(Args, JArg, OArg)), O).




%=======================================================================

%-----------------------------------------------------------
%　xy_justified_args(Args,J).
%　抽象論証集合(リスト)ArgsにおいてJはx/y正当化された論証の集合である．

xy_justified_args(Args,J) :- 
	collect_xy_justified_args(Args, [], J).


%-----------------------------------------------------------
%　collect_xy_justified_args(Args, S, J)
%　抽象論証集合(リスト)Argsにおいて，
%　抽象論証集合Sから初めて反復的にx/y受理可能な論証の集合を求めたとき，
%　最初に見つかる不動点はJである．
%　（もし，Sが空ならJは最小不動点．）

collect_xy_justified_args(Args, S, J) :- 
	setof(Arg, xy_acceptable(Args, Arg, S), AcceptedArgs),
	AcceptedArgs \== S,
	collect_xy_justified_args(Args, AcceptedArgs, J) .

collect_xy_justified_args(Args, S, S) :- 
	setof(Arg, xy_acceptable(Args, Arg, S), AcceptedArgs),
	AcceptedArgs == S.


%-----------------------------------------------------------
%　xy_acceptable(Args, Arg, S)
%　抽象論証集合(リスト)Argsにおいて，ArgはSに関して受理可能である．

xy_acceptable(Args, Arg, S):-
	member(Arg, Args),
	findall(Xattacker, x_attack(Args, Xattacker, Arg), Xattackers),
	all_y_attack(Args, Xattackers, S).


%-----------------------------------------------------------
%　all_y_attack(Args, Xattackers, S)
%　抽象論証集合(リスト)Argsにおいて，
%　Xattackersの中のすべての論証はSの中のある論証によってy攻撃されている．

all_y_attack(Args, [Arg1 | Other], S) :- 
	member(Arg2, S),	
	y_attack(Args, Arg2, Arg1),
	all_y_attack(Args, Other, S).

all_y_attack(_, [], _).


%-----------------------------------------------------------
% x(Args, Arg1, Arg2)　
% 抽象論証Arg1とArg2は抽象論証集合(リスト)Argsの要素であり，
% Arg1はArg2をx攻撃している．
% （ユーザーによってx_attack/2が定義されていないといけない）

x_attack(Args, Arg1, Arg2) :- 
	member(Arg1, Args),
	member(Arg2, Args),
	x_attack(Arg1, Arg2). 


%-----------------------------------------------------------
% y(Args, Arg1, Arg2)　
% 抽象論証Arg1とArg2は抽象論証集合(リスト)Argsの要素であり，
% Arg1はArg2をy攻撃している．
% （ユーザーによってy_attack/2が定義されていないといけない）

y_attack(Args, Arg1, Arg2) :- 
	member(Arg1, Args),
	member(Arg2, Args),
	y_attack(Arg1, Arg2).




%=======================================================================
%対話的証明論

%-----------------------------------------------------------
%  provably_xy_justified(Arg)
%　Argは証明論的にx/y正当化された論証である．

provably_xy_justified(Arg) :- 
	move_P(Arg, [Arg]).


%-----------------------------------------------------------
%  move_P(Arg, PLog)
%　定義(P, Arg)を根としたx/y勝利対話木が存在する．
%  ただし，対話木の中で提案者はPLogに含まれる論証を根以外に使用しない．
% （ユーザーによってfind_x_attacker/2が定義されていないといけない）

move_P(Arg, PLog) :-
	findall(Xattacker, find_x_attacker(Xattacker, Arg), Xattackers),
	move_O(Xattackers, PLog).


%-----------------------------------------------------------
%  move_O(Args, PLog)
%　Argsに含まれるすべての論証に対し，
%　それをy攻撃するような論証を根としたx/y勝利対話木が存在する．
%  ただし，対話木の中で提案者はPLogに含まれる論証を使用しない．
% （ユーザーによってfind_y_attacker/2が定義されていないといけない）

move_O([Arg | Other], PLog) :- 
	find_y_attacker(Yattacker, Arg),
	non_member(Yattacker, PLog),		%←提案者の二度出し禁止チェック
	append([Yattacker], PLog, NewPLog),
	move_P(Yattacker, NewPLog),
	move_O(Other, PLog).

move_O([], _).


%=======================================================================
%対話的証明論　対話木作成機能付きバージョン

%-----------------------------------------------------------
%  provably_xy_justified2(Arg, Tree)
%　Argは証明論的にx/y正当化された論証である．
%  (提議(P, Arg)を根としたx/y勝利対話木Treeが存在する．)


provably_xy_justified2(Arg, Tree) :- 
	move_P2(Arg, [Arg], Tree).


%-----------------------------------------------------------
%  move_P2(Arg, PLog, Tree)
%　定義(P, Arg)を根としたx/y勝利対話木Treeが存在する．
%  ただし，対話木の中で提案者はPLogに含まれる論証を根以外に使用しない．
% （ユーザーによってfind_x_attacker/2が定義されていないといけない）

move_P2(Arg, PLog, Tree) :-
	findall(Xattacker, find_x_attacker(Xattacker, Arg), Xattackers),
	move_O2(Xattackers, PLog, Children),
	append([(p,Arg)],Children,Tree).


%-----------------------------------------------------------
%  move_O2(Args, PLog, Trees)
%　Argsに含まれるすべての論証に対し，
%　それをy攻撃するような論証を根としたx/y勝利対話木が存在する
%　(Treesはその対話木のリストである)．
%  ただし，対話木の中で提案者はPLogに含まれる論証を使用しない．
% （ユーザーによってfind_y_attacker/2が定義されていないといけない）

move_O2([Arg | Other], PLog, Trees) :- 
	find_y_attacker(Yattacker, Arg),
	non_member(Yattacker, PLog),		%←提案者の二度出し禁止チェック
	append([Yattacker], PLog, NewPLog),
	move_P2(Yattacker, NewPLog, Tree),
	move_O2(Other, PLog, OtherTrees),
	append([[(o,Arg), Tree]],OtherTrees,Trees).

move_O2([], _, []).

%=======================================================================
%　ユーザー定義のサンプル

%　x攻撃とy攻撃の定義
%　x_attack/2とy_attack/2は論証が単一化された状態で呼ばれる．
%　すなわち，変数のまま呼ばれることはないので，内部で論証を生成する必要はない．
%　find_x_attacker/2とfind_y_attacker/2は第一引数の論証が変数の状態で呼ばれる．
%　すなわち，Arg2をx(あるいはy)攻撃できるような論証を内部で生成し，
%　単一化してやる必要がある．


%x_attack(Arg1, Arg2) :- attack(Arg1, Arg2).
%y_attack(Arg1, Arg2) :- undercut(Arg1, Arg2).

%find_x_attacker(Arg1, Arg2) :- x_attack(Arg1, Arg2).
%find_y_attacker(Arg1, Arg2) :- y_attack(Arg1, Arg2).


%attack(Arg1,Arg2) :- rebut(Arg1,Arg2).
%attack(Arg1,Arg2) :- undercut(Arg1,Arg2).

%rebut(arg1,arg2).
%rebut(arg3,arg2).
%rebut(arg7,arg6).
%undercut(arg4,arg1).
%undercut(arg4,arg3).
%undercut(arg2,arg5).
%undercut(arg2,arg7).
%undercut(arg5,arg6).

%undercut(arg8,arg9).
%undercut(arg9,arg8).




%undercut(arg2,arg3).
%undercut(arg2,arg6).
%undercut(arg3,arg2).
%undercut(arg3,arg5).
%undercut(arg4,arg1).
%undercut(arg4,arg5).
%undercut(arg5,arg3).
%undercut(arg5,arg6).
%undercut(arg6,arg2).
%undercut(arg6,arg5).




%=======================================================================
%　実行例
%　
%　| ?- xy_argumentation([arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9],J,O,D).

%　D = [arg8,arg9],

%　J = [arg2,arg4,arg6],

%　O = [arg5,arg7,arg1,arg3] ? 

%　yes 
%
%　| ?- member(X,[arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9]), provably_xy_justified(X).
%　X = arg2 ? ;

%　X = arg4 ? ;

%　X = arg6 ? 

%　yes

%
