### 목차
- [셸의 기능과 종류](#셸의-기능과-종류)
- [셸 기본 사용법](#셸-기본-사용법)
- [입출력 방향 바꾸기](#입출력-방향-바꾸기)
- [배시 셸 환경 설정](#배시-셸-환경-설정)
- [에일리어스와 히스토리](#에일리어스와-히스토리)

## 셸의 기능과 종류
### 셸
사용자와 리눅스 커널 사이에서 중간자 역할을 수행한다.
### 셸의 기능
- 명령어 해석기 기능 : 사용자와 커널 사이에서 명령을 해석하여 전달하는 해석기와 번역기 기능
    - 예를 들어, "로그인 셸" : 사용자가 로그인하면 셸이 자동으로 실행되어 사용자가 명령을 입력하기를 기다린다.   
- 프로그래밍 기능 : 여러 명령을 사용하여 반복적으로 수행하는 작업을 하나의 프로그램으로 만들 수 있다. (-> 셸 스크립트라고 한다.)
- 사용자 환경 설정 기능 : 초기화 파일 기능(기본 권한 설정, 환경변수 설정 등)을 제공하여, 사용자별 특성에 맞게 초기 환경을 셋팅할 수 있다.
### 셸의 종류
본 셸, C 셸, 콘 셸, 배시 셸 등이 있다.
- 본 셸 : 최초의 셸로, 현재도 수 많은 셸 스크립트는 본 셸을 기반으로 한다. 본셸의 명령 이름은 sh이다.
- 배시 셸 : 본 셸을 기반으로 개발된 셸로 호환성이 유지되며, 배시 셸의 명령 이름은 bash이다. 배시 셸은 GPL을 따르는 공개 소프트웨어로서 리눅스의 기본 셸로 제공되어 리눅스 셸로도 많이 알려졌다. 우분투도 배시 셸을 기본 셸로 사용하고 있다.
- 대시 셸 : 본 셸을 기반으로 개발되었으며 우분투는 6.10 버전부터 본 셸 대신 대시 셸을 기본 셸로 사용하고 있다.


## 셸 기본 사용법
리눅스의 기본 셸이면서 풍부한 기능을 제공하는 배시 셸을 중심으로 기본 사용법을 알아본다.
### 셸 확인하기
- 프롬포트 모양으로 셸 종류 확인이 가능하며, 배시 셸은 $이 기본 프롬포트 이다.
- 특정 사용자 정보(/etc/passwd)를 찾아 확인도 가능하다.
```
// 배시 셸을 사용 중임을 확인할 수 있다.
user1@myubuntu:~$ grep ubuntu /etc/passwd
user1:x:1000:1001:user1:/home/user1:/bin/bash
```
### 기본 셸 바꾸기
우분투의 기본 셸인 배시 셸 말고 다른 셸을 기본 셸로 이용하고하 할 경우, 리눅스에 해당 셸을 설치하고 chsh라는 명령어를 통해 사용자의 로그인 셸을 변경할 수 있다.
> chsh -s 지정하는셸(절대경로)

### 출력 명령
배시 셸의 출력 명령은 echo와 printf가 있다.
#### echo [문자열]
- 화면에 한 줄의 문자열을 출력한다.
- 모든 셸에서 공통적으로 제공하는 출력 명령이다.
```
user1@myubuntu:~$ echo

user1@myubuntu:~$ echo text
text
user1@myubuntu:~$ echo "text"
text
```
#### printf 형식 [인수...]
- 자료를 형식화하여 화면에 출력한다.
```
user1@myubuntu:~$ printf linux
linuxuser1@myubuntu:~$ printf "ubuntu linux\n"
ubuntu linux
user1@myubuntu:~$ printf "%d+%d=%d\n" 10 10 20
10+10=20
```

### 특수 문자 사용하기
|특수문자|기능|예|
|---|----------------------|------------------------|
|*(별표)|임의의 문자 또는 문자열|ls *.txt : 확장자가 txt인 모든 파일 나열|
|?(물음표)|임의의 한 문자|ls t?.txt : t1.txt, ta.txt가 해당되며 t.txt는 제외|
|\[](대괄호)|대괄호 안에 포함된 문자중 하나|ls [0-9]* : 파일명이 숫자로 시작하는 모든 파일 나열|
|~(물결표)|홈 디렉터리|cd ~[사용자ID] : 해당 사용자의 홈 디렉터리로 이동한다.|
|-(붙임표)|디렉터리를 이전하기 직전의 작업 디렉터리|cd - : 이전 작업 디렉터리로 이동한다.|
|;(쌍반점)|연결된 명령을 왼쪽부터 차례로 실행|date;ls;pwd : 날짜출력 후, 디렉터리 파일 출력 후, 절대경로 출력|
|\|(파이프)|왼쪽 명령의 실행 결과를 오른쪽 명령의 입력으로 전달|ls -al / \| more : ls -al / 명령의 결과가 more 명령의 입력으로 전달되어 페이지 단위로 출력된다.|
|''(작은따옴표)|모든 셸의 문자를 무시|echo '$PATH' : 문자열 $PATH가 출력된다.|
|""(큰따옴표)|$, ``, \를 제외한 특수문자를 무시|echo "$PATH" : 셸 환경변수인 PATH에 저장된 값이 출력된다.(/bin/bash 출력)|
|\`\`(백쿼터)|명령으로 해석하여 실행한다.|echo "Today is \`date\`" : Today is Sun Jan  8 22:52:17 KST 2023 이 출력된다.|
|\\(역빗금)|특수 문자 바로 앞에 사용되며, 해당 특수 문자의 효과를 없애고 일반 문자로 처리한다.|echo \$SHELL : 문자열 $SHELL이 출력된다.|


## 입출력 방향 바꾸기


## 배시 셸 환경 설정
### 셸 변수와 환경 변수
셸 변수는 각 셸별로 따로 지정되어 독립적인 반면, 환경 변수는 로그인 셸과 서브 셸에 모두 공통적으로 적용된다.
- 셸 변수 : 현재 셸에서만 사용
- 환경 변수 : 현재 셸뿐만 아니라 서브 셸로도 전달  
셸 변수는 각 셸별로 따로 지정되어 독립적인 반면, 환경 변수는 로그인 셸과 서브 셸에 모두 공통적으로 적용
#### 주요 환경 변수
- HISTSIZE : 히스토리 저장 크기
- HOME : 사용자 홈 디렉터리의 절대 경로
- LANG : 사용하는 언어
- LOGNAME : 사용자 계정 이름
- PATH : 명령을 탐색할 경로
- PWD : 작업 디렉터리 절대 경로
- SHELL : 로그인 셸
```
mimdong@mimdongs-MacBook-Pro book % echo $HISTSIZE
2000
mimdong@mimdongs-MacBook-Pro Pem % echo $PWD
/Users/mimdong/Pem
mimdong@mimdongs-MacBook-Pro Pem % echo $SHELL
/bin/zsh
mimdong@mimdongs-MacBook-Pro Pem % echo $HOME
/Users/mimdong
mimdong@mimdongs-MacBook-Pro Pem % echo $LANG
ko_KR.UTF-8
mimdong@mimdongs-MacBook-Pro Pem % echo $LOGNAME
mimdong
mimdong@mimdongs-MacBook-Pro Pem % echo $PATH
/opt/homebrew/bin:/opt/homebrew/sbin:/usr/local/bin:/System/Cryptexes/App/usr/bin:/usr/bin:/bin:/usr/sbin:/sbin:/opt/homebrew/bin:/opt/homebrew/sbin:/Users/mimdong/Library/Application Support/JetBrains/Toolbox/scripts:/Users/mimdong/Library/Application Support/JetBrains/Toolbox/scripts
mimdong@mimdongs-MacBook-Pro Pem % echo $PWD
/Users/mimdong/Pem
mimdong@mimdongs-MacBook-Pro Pem % echo $SHELL
/bin/zsh
```
### 변수 출력
- set : 셸 변수와 환경 변수 모두 출력
- env : 환경 변수만 모두 출력
- echo : 특정 변수 출력 (변수 앞에 $를 붙여야 한다.)
```
mimdong@mimdongs-MacBook-Pro book % echo $SHELL
/bin/zsh
```


## 에일리어스와 히스토리
### 에일리어스
명령에 다른 별칭을 붙인다.
- alias 이름='명령' : 에일리어스 생성
- unalias 에일리어스 : 에일리어스 해제

### 히스토리
사용한 명령을 기억했다가 재실행하는 기능
- history : 히스토리(명령 입력 기록)를 출력한다.
- !! : 바로 직전에 실행한 명령을 재실행한다.
- !번호 : 히스토리에서 해당 번호의 명령을 재실행한다.
- !문자열 : 히스토리에서 해당 문자열로 시작하는 마지막 명령을 재실행한다.