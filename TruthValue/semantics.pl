% 基本命令の定義.
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

% ベキ集合の定義.
powerSet([],[]).
powerSet([_|Xt],Y) :- powerSet(Xt,Y).
powerSet([Xh|Xt],[Xh|Yt]) :- powerSet(Xt,Yt).

% findallの定義.
%--------------------------------------------------------------------------
findall2(X, G, _) :-asserta(item(no_more)),call(G),asserta(item(X)),fail.
findall2(_, _, List) :- collect_item([], L), !, List = L.

collect_item(S, L) :- next_item(X), !,collect_item([X | S], L).
collect_item(L, L).

next_item(X) :- retract(item(X)), !,X \== no_more.
%--------------------------------------------------------------------------

% 変数をdynamic宣言.
%:- dynamic attack/2.
%:- dynamic item/2.

%sublist([], _).
%sublist(Sub, Ls) :- suffix(Ls, Ss), prefix(Ss, Sub), Sub \= [].

%suffix(Xs, Ys) :- append(_, Ys, Xs).
%prefix(Xs, Ys) :- append(Ys, _, Xs).

% 差集合の定義.
difference([], _, []).
difference([X | Xs], Ys, Zs) :- member(X, Ys), !, difference(Xs, Ys, Zs).
difference([X | Xs], Ys, [X | Zs]) :- difference(Xs, Ys, Zs).

% Xから重複を削除したYを生成する．
set_of_list([], []).
set_of_list([X | Xs], Ys) :- member(X, Xs), !, set_of_list(Xs, Ys).
set_of_list([X | Xs], [X | Ys]) :- set_of_list(Xs, Ys).

% X のすべての部分集合をLsに得る.(破棄)
%allSubset(X,Ls) :- findall(Y,powerSet(Y,X),Ls).

% 二つの集合が共通部分を持たないか判定.(破棄)
%--------------------------
% differentSet([1,2,3],[4,5]).
%--------------------------
% true.
%--------------------------
%differentSet([],_).
%differentSet([Xh|Xt],Y) :- not(member(Xh,Y)),differentSet(Xt,Y).

% 攻撃関係の追加.(知識の追加)
%--------------------------
% addAttacks([[0,1],[1,2]])
%--------------------------
% attack(0,1).
% attack(1,2).
%--------------------------
addAttacks([]).
addAttacks([[Yh|[Yt]]|Xt]) :- assert(attack(Yh,Yt)),addAttacks(Xt).

% 集合の中に攻撃関係が含まれるか判定.
%**************************
% attack(1,2).
%**************************
% checkAttack([1,2,3].
%--------------------------
% false.
%--------------------------
check([],_).
check([Xh|Xt],Y) :- findall(Z,attack(Xh,Z),Ls),!,difference(Y,Ls,Y),!,check(Xt,Y).
checkAttack(Z) :- check(Z,Z).

% attackを含まないXの部分集合Yを返す.
%**************************
% attack(1,2).
% attack(2,0).
%**************************
% free([0,1,2],Y).
%--------------------------
% Y = [].
% Y = [0].
% Y = [0,1].
% Y = [1].
% Y = [2].
%--------------------------
free(X,Y) :- powerSet(X,Y),checkAttack(Y).

% コンフリクトフリーなリストを得る.
%**************************
% attack(1,2).
%**************************
% conflictFree([0,1,2],X).
%--------------------------
% X = [[],[0],[0,1],[2]].
%--------------------------
conflictFree(X,Y) :- findall(Z,free(X,Z),Y).

% 防御可能か判定.defend(attackers,recievers).
%**************************
% attack(0,1).
% attack(3,0).
%**************************
% defend([0],[1,3]).
%--------------------------
% true.
%--------------------------
defend([],_).
defend([Xh|Xt],Y) :- attack(Z,Xh),member(Z,Y),defend(Xt,Y).

% Xの要素すべてがXに関して受理可能であるか判定する.
%**************************
% attack(0,1).
% attack(3,0).
%**************************
% acceptable([1,3]).
%--------------------------
% true.
%--------------------------
accept([],_).
accept([Xh|Xt],Z) :- not(attack(_,Xh)),!,accept(Xt,Z).
accept([Xh|Xt],Z) :- findall(Y,attack(Y,Xh),Ls),!,difference(Ls,Z,W),defend(W,Z),!,accept(Xt,Z).
acceptable(X) :- accept(X,X).

% Xのadmissible set を求める.
%**************************
% attack(0, 1).
% attack(1, 0).
% attack(1, 3).
% attack(2, 0).
% attack(3, 0).
%**************************
% admissible([0,1,2,3],X).
%--------------------------
% X = [[], [1], [1, 2], [2]].
%--------------------------
adm(X,Y) :- free(X,Y),acceptable(Y).
admissible(X,Ls) :- findall(Y,adm(X,Y),Ls).

% completeExtension を求める.
%**************************
% attack(a,b).
% attack(b,a).
% attack(b,c).
% attack(c,d).
% attack(d,c).
%**************************
% completeExtension([a,b,c,d],X).
%--------------------------
% X = [[], [d], [b, d], [a], [a, d], [a, c]].
%--------------------------
complete(X,Ls) :- adm(X,Y),invalid(Y,Z),difference(X,Z,W),unattacks(W,Ls),subset(Ls,Y).
completeExtension(X,Ls) :- findall(Y,complete(X,Y),Ls).


% Xを攻撃する要素がYにあるか判定．(ないときtrue.)
%**************************
% attack(0, 1).
% attack(1, 0).
% attack(1, 3).
% attack(2, 0).
% attack(3, 0).
%**************************
% attackMember(2,[1,2,3]).
%--------------------------
% true.
%--------------------------
attackMember(X,_) :- not(attack(_,X)).
attackMember(X,Y) :- findall(W,attack(W,X),Ls),difference(Y,Ls,Y).

% 攻撃を受けていないリストを求める.
%**************************
% attack(0, 1).
% attack(1, 0).
% attack(1, 3).
% attack(2, 0).
% attack(3, 0).
%**************************
% unattacks([0,1,2,3,4],X).
%--------------------------
% X = [2,4].
%--------------------------
unattack([],_,[]).
unattack([Xh|Xt],Z,[Xh|Y]) :- attackMember(Xh,Z),unattack(Xt,Z,Y).
unattack([_|Xt],Y,Z) :- unattack(Xt,Y,Z).
unattacks(X,Y) :- unattack(X,X,Y),!.

% Xによって無効化されるリストを求める.
%**************************
% attack(0, 1).
% attack(1, 0).
% attack(1, 3).
% attack(2, 0).
% attack(3, 0).
%**************************
% invalid([2,3],Y).
%--------------------------
% Y = [0].
%-------------------------- 
invalid([],[]).
invalid([Xh|Xt],W) :- findall(T,attack(Xh,T),Y),invalid(Xt,Z),append(Y,Z,S),sort(S,W).

% groundedExtensionを計算する.
%**************************
% attack(a, b).
% attack(b, c).
% attack(c, d).
% attack(e, f).
% attack(f, e).
%**************************
% groundedExtension([a,b,c,d,e,f],X).
%--------------------------
% X = [a,c].
%-------------------------- 
ground(X,Z) :- unattacks(X,Ls),invalid(Ls,W),!,difference(X,W,Y),Z=Y.
groundedExtension(X,Ls) :- ground(X,Z),X==Z,unattacks(Z,Y),Ls=Y,!.
groundedExtension(X,Ls) :- ground(X,Z),groundedExtension(Z,Ls),!.

% stableExtensionを計算する.
%**************************
% attack(a, b).
% attack(b, c).
% attack(c, d).
% attack(e, f).
% attack(f, e).
%**************************
% stableExtension([a,b,c,d,e,f],X).
%--------------------------
% X = [[a,c,e],[a,c,f]].
%-------------------------- 
stable(X,Ls) :- free(X,Y),difference(X,Y,Z),invalid(Y,Z),Ls=Y.
stableExtension(X,Ls) :- findall(Y,stable(X,Y),Z),isOne(Z,Ls).

% Xの集合の要素が1つだったら外側の[]をはずす．
isOne([Xh|Xt],Ls) :- Xt==[],Ls=Xh,!.
isOne(X,Ls) :- Ls=X,!.

% XがYのリストの中で包含関係に関して極大か判定する．
%**************************
% attack(a, b).
% attack(b, c).
% attack(c, d).
% attack(d, c).
% attack(d, e).
% attack(e, e).
%**************************
% maximal([a],[[a,d],[a,c]]).
%--------------------------
% false.
%-------------------------- 
maximal(_,[]).
maximal(X,[Yh|Yt]) :- not(subset(X,Yh)),maximal(X,Yt),!.

% preferredExtensionを計算する．
%**************************
% attack(a, b).
% attack(b, c).
% attack(c, d).
% attack(d, c).
% attack(d, e).
% attack(e, e).
%**************************
% preferredExtension([a,b,c,d,e],X).
%--------------------------
% X = [[a, d], [a, c]].
%-------------------------- 
preferred([],[],[]).
preferred([Xh|Xt],[_|Yt],[Xh|Ls]) :- maximal(Xh,Yt),preferred(Xt,Yt,Ls).
preferred([_|Xt],[_|Yt],Ls) :- preferred(Xt,Yt,Ls).
preferredExtension(X,Ls) :- admissible(X,Y),preferred(Y,Y,Z),isOneP(Z,Ls),!.

isOneP([[]],[[]]).
isOneP([Xh|Xt],Ls) :- Xt==[],Ls=Xh,!.
isOneP(X,Ls) :- Ls=X,!.


% Logを書き出す．
%writeLog(X) :- tell('log.txt'),write(X),told.

% 以下，テスト用.
%climb :- addAttacks([[0,12],[0,13],[1,14],[3,13],[4,13],[5,12],[7,14],[8,14],[10,15],[12,0],[13,0],[14,0],[14,1],[15,4]]).
af2 :- addAttacks([[a,b],[b,a]]).
%af3 :- addAttacks([[b,a]]).
%af4 :- addAttacks([[a,b],[b,a],[b,c]]).
af5 :- addAttacks([[d,c],[c,d],[b,c],[b,a],[a,b]]).
af6 :- addAttacks([[a,b],[b,c],[c,d],[d,e],[e,f],[f,e]]).
%af7 :- addAttacks([[a,b],[b,c],[c,a]]).
af10 :- addAttacks([[a,b],[b,c],[c,d],[d,c],[d,e],[e,e]]).
%removeattacks :- retractall(attack(_,_)).
