### 목차
- [사용자 계정 관련 파일](#사용자-계정-관련-파일)
- [사용자 계정 관리 명령](#사용자-계정-관리-명령)
- [그룹 관리 명령](#그룹-관리-명령)
- [사용자 정보 관리 명령](#사용자-정보-관리-명령)

## 사용자 계정 관련 파일
### /etc/passwd 파일
사용자 계정 정보가 저장된 기본 파일, 초기에는 사용자 암호도 같이 저장했으나 해킹 위험이 증가하여 암호는 /etc/shadow 파일에 별도로 저장한다.  
한 행에 사용자 한명에 대한 정보가 기록되며, 쌍점(:)으로 구분되는 일곱개의 항목으로 구성되어 있다.
- 로그인ID : 사용자 계정 이름
- x : 암호를 저장하던 항목이나, 보안상의 이유로 /etc/shadow 파일에 별도 보관한다. 이전 프로그램과의 호환성 유지를 위해 그대로 유지하고 x 로 표기한다.
- UID : 사용자 ID 번호로 시스템이 사용자를 구별하기 위해 사용하는 번호이다.
- GID : 그룹 ID 이다. 리눅스에서 사용자는 무조건 하나 이상의 그룹에 소속된다.  
사용자의 기본 그룹은 사용자를 등록할 때 정해지고, 특별히 소속 그룹을 지정하지 않으면 자동적으로 로그인ID가 그룹으로 등록된다. 그룹에 대한 정보는 /etc/group 파일에 저장되어 있다.
- 설명 : 사용자의 실명이나 부서명, 연락처 등 사용자에 대한 일반적인 정보가 기록되는 부분
- 홈 디렉터리 : 사용자 계정에 할당된 홈 디렉터리의 절대 경로이다. 사용자가 로그인할 때 자동적으로 로그인 되는 디렉터리를 홈 디렉터리라고 한다.
- 로그인 셸 : 로그인 셸은 사용자가 로그인할 때 기본적으로동작하는 셸로, 현재 우분투에서는 배시 셸(/bin/bash)을 기본 셸로 사용하고 있다.
### /etc/shadow 파일
사용자 암호에 관한 정보를 별도로 관리하는 파일로, 해당 파일은 root 사용자만 읽고 쓸 수 있다.
### /etc.login.defs 파일
사용자 계정의 설정과 관련된 기본 값을 정의한 파일이다.
### /etc/groups 파일
사용자가 속한 그룹 중 /etc/passwd 파일의 GID 항목에 지정된 그룹이 기본 그룹이고, 사요자가 속한 2차 그룹은 이 파일에 지정한다.
### /etc/gshadow 파일
그룹의 암호가 저장된 파일 이다.
### /etc/skel
사용자 계정 생성 시 공통으로 배포할 파일이나 디렉터리를 저장한다. 즉, 사용자 생성 시 /etc/skell 디렉터리가 자동으로 복사된다.

## 사용자 계정 관리 명령
### 사용자 계정 생성하기
> useradd [옵션] 로그인ID  
> 사용자 계정을 생성한다.  
>> 옵션   
>> -d 디렉터리명 : 홈 디렉터리 지정  
>> -m : 홈 디렉터리를 생성한다.  
>> -s 셸 : 기본 셸을 지정한다.
## 사용자 계정 수정/삭제 명령
usermod 명령은 사용자 계정 정보를 수정할 때 사용하고, userdel 명령은 사용자 계정을 삭제할 때 사용한다. 

## 그룹 관리 명령
- groupadd : 새로운 그룹 생성
- groupmod : 그룹 정보 수정
- groupdel : 그룹 삭제
- gpasswd : 그룹 암호 설정 및 그룹에 멤버 추가/삭제
- newgrp : 사용자자의 기본 그룹을 다른 그룹으로 변경

## 사용자 정보 관리 명령
### UID와 EUID
- UID : 사용자가 로그인할 때 사용한 계정의 UID
- EUID(effective UID) : 현재 명령을 수행하는 주체의 UID  
- 일반적으로 UID와 EUID는 같은데 달라지는 경우가 있다. (예. su명령으로 다른 계정으로 전환한 경우)
- UID 출력 명령 : who am i, who -m
- EUID 출력 명령 : whoami, id
### 사용자 관련 명령
> who  
> 현재 시스템에 로그인한 사용자의 정보 확인

> w  
> 현재 시스템에 로그인한 사용자 정보 외에, 사용자가 무엇을 하고 있는지 실행중인 작업 정보 출력

> last  
> 사용자의 이름과 로그인한 시간, 로그아웃 시간, 터미널 번호나 IP 주소 출력한다.  
> 즉, 누가 언제 로그인해서 로그아웃했는지를 알 수 있다.

> groups  
> 사용자 계정이 속한 그룹을 출력

> passwd [옵션] [사용자계정]  
> 사용자 계정의 암호를 수정한다.  
>> 옵션  
>> -l 사용자계정 : 지정한 계정의 암호를 잠근다.  
>> -u 사용자계정 : 암호 잠금을 해제한다.  
>> -d 사용자계정 : 지정한 계정의 암호를 삭제한다.

> chown [옵션] 사용자계정 파일명/디렉터리명  
> 파일과 디렉터리의 소유자와 소유 그룹을 변경한다.  
>> 옵션  
>> -R : 서브 디렉터리의 소유자와 소유 그룹도 변경한다.

> chgrp  
> 파일과 디렉터리의 소유 그룹 변경

### root 권한 사용하기
일반 사용자가 root 권한을 사용할 수 있는 방법은 두가지가 있다.  
첫번째 방법은 su 명령을 사용하여 root 계정으로 전환하는 것이다. 이는 간단하지만 root계정으로 전환하기 때문에 모든 권한을 가지게 되어 보안상 매우 위험한 방법이다.  
두번째 방법은 일반 사용자에게 시스템 관리 작업 중 특정 작업만 수행할 수 있는 권한을 주는 것이다. sudo 명령으로 이와 같은 제한적인 권한 부여가 가능하다.
- sudo 권한 설정하기  
일반 사용자가 sudo 명령으로 root 권한을 실행하려면 특정 권한을 부여 받아야 하며, 이 권한은 /etc/sudoers 파일에 설정한다.
- /etc/sudoers의 설정 형식
```
사용자계정 호스트=명령어

## root의 경우
root    ALL=(ALL:ALL) ALL

## user2의 특정 명령을 수행할 수 있는 권한 부여 및 sudo 사용시 암호 입력 과정 생략
user2   ALL=NOPASSWD:/sbin/useradd, /sbin/usermod
```