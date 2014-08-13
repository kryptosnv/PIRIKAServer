%���ۋc�_�t���[�����[�N
%-----------------------------------------------------------

:- use_module(library(lists)).
%=======================================================================

%-----------------------------------------------------------
%�@xy_Argumentation(Args, J, O, D).
%�@���ۘ_�؏W��(���X�g)Args�ɂ����āC
%�@J��x/y���������ꂽ�_�؂̏W���ł���C
%�@O��x/y�p�����ꂽ�_�؂̏W���ł���D
%�@D��x/y�h��\�Ș_�؂̏W���ł���D


xy_argumentation(Args, J, O, D) :-
	xy_justified_args(Args,J), 
	xy_overruled_args(Args, J, O),
	xy_defensible_args(Args, J, O, D).




%=======================================================================

%-----------------------------------------------------------
%�@xy_defensible_args(Args, J, O, D).
%�@���ۘ_�؏W��(���X�g)Args�ɂ����āC
%�@J��x/y���������ꂽ�_�؂̏W���CO��x/y�p�����ꂽ�_�؂̏W���Ƃ����Ƃ��C
%�@D��x/y�h��\�Ș_�؂̏W���ł���D

xy_defensible_args(Args, J, O, D) :- 
	append(J, O, JO),
	remove_list(Args, JO, D).


%-----------------------------------------------------------
%�@remove_list(List, Remove, Result).
%�@List����Remove�̂��ׂĂ̗v�f���폜�������ʂ�Result�ł���D

remove_list(List, [X | Other], Result) :-
	delete(List, X, ListwithoutX),
	remove_list(ListwithoutX, Other, Result).

remove_list(List, [], List).
	



%=======================================================================

%-----------------------------------------------------------
%�@xy_overruled_args(Args, J, O).
%�@���ۘ_�؏W��(���X�g)Args�ɂ����āCJ��x/y���������ꂽ�_�؂̏W���Ƃ����Ƃ��C
%�@O��x/y�p�����ꂽ�_�؂̏W���ł���D

xy_overruled_args(Args, J, O) :- 
	findall(OArg, JArg^(member(JArg, J), y_attack(Args, JArg, OArg)), O).




%=======================================================================

%-----------------------------------------------------------
%�@xy_justified_args(Args,J).
%�@���ۘ_�؏W��(���X�g)Args�ɂ�����J��x/y���������ꂽ�_�؂̏W���ł���D

xy_justified_args(Args,J) :- 
	collect_xy_justified_args(Args, [], J).


%-----------------------------------------------------------
%�@collect_xy_justified_args(Args, S, J)
%�@���ۘ_�؏W��(���X�g)Args�ɂ����āC
%�@���ۘ_�؏W��S���珉�߂Ĕ����I��x/y�󗝉\�Ș_�؂̏W�������߂��Ƃ��C
%�@�ŏ��Ɍ�����s���_��J�ł���D
%�@�i�����CS����Ȃ�J�͍ŏ��s���_�D�j

collect_xy_justified_args(Args, S, J) :- 
	setof(Arg, xy_acceptable(Args, Arg, S), AcceptedArgs),
	AcceptedArgs \== S,
	collect_xy_justified_args(Args, AcceptedArgs, J) .

collect_xy_justified_args(Args, S, S) :- 
	setof(Arg, xy_acceptable(Args, Arg, S), AcceptedArgs),
	AcceptedArgs == S.


%-----------------------------------------------------------
%�@xy_acceptable(Args, Arg, S)
%�@���ۘ_�؏W��(���X�g)Args�ɂ����āCArg��S�Ɋւ��Ď󗝉\�ł���D

xy_acceptable(Args, Arg, S):-
	member(Arg, Args),
	findall(Xattacker, x_attack(Args, Xattacker, Arg), Xattackers),
	all_y_attack(Args, Xattackers, S).


%-----------------------------------------------------------
%�@all_y_attack(Args, Xattackers, S)
%�@���ۘ_�؏W��(���X�g)Args�ɂ����āC
%�@Xattackers�̒��̂��ׂĂ̘_�؂�S�̒��̂���_�؂ɂ����y�U������Ă���D

all_y_attack(Args, [Arg1 | Other], S) :- 
	member(Arg2, S),	
	y_attack(Args, Arg2, Arg1),
	all_y_attack(Args, Other, S).

all_y_attack(_, [], _).


%-----------------------------------------------------------
% x(Args, Arg1, Arg2)�@
% ���ۘ_��Arg1��Arg2�͒��ۘ_�؏W��(���X�g)Args�̗v�f�ł���C
% Arg1��Arg2��x�U�����Ă���D
% �i���[�U�[�ɂ����x_attack/2����`����Ă��Ȃ��Ƃ����Ȃ��j

x_attack(Args, Arg1, Arg2) :- 
	member(Arg1, Args),
	member(Arg2, Args),
	x_attack(Arg1, Arg2). 


%-----------------------------------------------------------
% y(Args, Arg1, Arg2)�@
% ���ۘ_��Arg1��Arg2�͒��ۘ_�؏W��(���X�g)Args�̗v�f�ł���C
% Arg1��Arg2��y�U�����Ă���D
% �i���[�U�[�ɂ����y_attack/2����`����Ă��Ȃ��Ƃ����Ȃ��j

y_attack(Args, Arg1, Arg2) :- 
	member(Arg1, Args),
	member(Arg2, Args),
	y_attack(Arg1, Arg2).




%=======================================================================
%�Θb�I�ؖ��_

%-----------------------------------------------------------
%  provably_xy_justified(Arg)
%�@Arg�͏ؖ��_�I��x/y���������ꂽ�_�؂ł���D

provably_xy_justified(Arg) :- 
	move_P(Arg, [Arg]).


%-----------------------------------------------------------
%  move_P(Arg, PLog)
%�@��`(P, Arg)�����Ƃ���x/y�����Θb�؂����݂���D
%  �������C�Θb�؂̒��Œ�Ď҂�PLog�Ɋ܂܂��_�؂����ȊO�Ɏg�p���Ȃ��D
% �i���[�U�[�ɂ����find_x_attacker/2����`����Ă��Ȃ��Ƃ����Ȃ��j

move_P(Arg, PLog) :-
	findall(Xattacker, find_x_attacker(Xattacker, Arg), Xattackers),
	move_O(Xattackers, PLog).


%-----------------------------------------------------------
%  move_O(Args, PLog)
%�@Args�Ɋ܂܂�邷�ׂĂ̘_�؂ɑ΂��C
%�@�����y�U������悤�Ș_�؂����Ƃ���x/y�����Θb�؂����݂���D
%  �������C�Θb�؂̒��Œ�Ď҂�PLog�Ɋ܂܂��_�؂��g�p���Ȃ��D
% �i���[�U�[�ɂ����find_y_attacker/2����`����Ă��Ȃ��Ƃ����Ȃ��j

move_O([Arg | Other], PLog) :- 
	find_y_attacker(Yattacker, Arg),
	non_member(Yattacker, PLog),		%����Ď҂̓�x�o���֎~�`�F�b�N
	append([Yattacker], PLog, NewPLog),
	move_P(Yattacker, NewPLog),
	move_O(Other, PLog).

move_O([], _).


%=======================================================================
%�Θb�I�ؖ��_�@�Θb�؍쐬�@�\�t���o�[�W����

%-----------------------------------------------------------
%  provably_xy_justified2(Arg, Tree)
%�@Arg�͏ؖ��_�I��x/y���������ꂽ�_�؂ł���D
%  (��c(P, Arg)�����Ƃ���x/y�����Θb��Tree�����݂���D)


provably_xy_justified2(Arg, Tree) :- 
	move_P2(Arg, [Arg], Tree).


%-----------------------------------------------------------
%  move_P2(Arg, PLog, Tree)
%�@��`(P, Arg)�����Ƃ���x/y�����Θb��Tree�����݂���D
%  �������C�Θb�؂̒��Œ�Ď҂�PLog�Ɋ܂܂��_�؂����ȊO�Ɏg�p���Ȃ��D
% �i���[�U�[�ɂ����find_x_attacker/2����`����Ă��Ȃ��Ƃ����Ȃ��j

move_P2(Arg, PLog, Tree) :-
	findall(Xattacker, find_x_attacker(Xattacker, Arg), Xattackers),
	move_O2(Xattackers, PLog, Children),
	append([(p,Arg)],Children,Tree).


%-----------------------------------------------------------
%  move_O2(Args, PLog, Trees)
%�@Args�Ɋ܂܂�邷�ׂĂ̘_�؂ɑ΂��C
%�@�����y�U������悤�Ș_�؂����Ƃ���x/y�����Θb�؂����݂���
%�@(Trees�͂��̑Θb�؂̃��X�g�ł���)�D
%  �������C�Θb�؂̒��Œ�Ď҂�PLog�Ɋ܂܂��_�؂��g�p���Ȃ��D
% �i���[�U�[�ɂ����find_y_attacker/2����`����Ă��Ȃ��Ƃ����Ȃ��j

move_O2([Arg | Other], PLog, Trees) :- 
	find_y_attacker(Yattacker, Arg),
	non_member(Yattacker, PLog),		%����Ď҂̓�x�o���֎~�`�F�b�N
	append([Yattacker], PLog, NewPLog),
	move_P2(Yattacker, NewPLog, Tree),
	move_O2(Other, PLog, OtherTrees),
	append([[(o,Arg), Tree]],OtherTrees,Trees).

move_O2([], _, []).

%=======================================================================
%�@���[�U�[��`�̃T���v��

%�@x�U����y�U���̒�`
%�@x_attack/2��y_attack/2�͘_�؂��P�ꉻ���ꂽ��ԂŌĂ΂��D
%�@���Ȃ킿�C�ϐ��̂܂܌Ă΂�邱�Ƃ͂Ȃ��̂ŁC�����Ř_�؂𐶐�����K�v�͂Ȃ��D
%�@find_x_attacker/2��find_y_attacker/2�͑������̘_�؂��ϐ��̏�ԂŌĂ΂��D
%�@���Ȃ킿�CArg2��x(���邢��y)�U���ł���悤�Ș_�؂�����Ő������C
%�@�P�ꉻ���Ă��K�v������D


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
%�@���s��
%�@
%�@| ?- xy_argumentation([arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9],J,O,D).

%�@D = [arg8,arg9],

%�@J = [arg2,arg4,arg6],

%�@O = [arg5,arg7,arg1,arg3] ? 

%�@yes 
%
%�@| ?- member(X,[arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9]), provably_xy_justified(X).
%�@X = arg2 ? ;

%�@X = arg4 ? ;

%�@X = arg6 ? 

%�@yes

%
