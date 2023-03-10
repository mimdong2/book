### 목차
- [파일의 속성](#파일의-속성)
- [파일의 접근 권한](#파일의-접근-권한)
- [기호를 이용한 파일 접근 권한 변경](#기호를-이용한-파일-접근-권한-변경)
- [숫자를 이용한 파일 접근 권한 변경](#숫자를-이용한-파일-접근-권한-변경)
- [기본 접근 권한 설정](#기본-접근-권한-설정)
- [특수 접근 권한](#특수-접근-권한)

## 파일의 속성
ls -l 명령은 파일의 상세 정보를 출력하는데, 이 상세 정보가 파일의 속성이다.
```
user1@ubuntu ~ % ls -l /etc/hosts
-rw-r--r--  1 root  root  213 12 21 09:06 /etc/hosts
```
|번호|속성 값|의미|
|---|-----------|--------------|
|1|-|파일의 종류(-:일반파일, d:디렉터리)|
|2|rw-r--r--|파일을 읽고, 쓰고, 실행할 수 있는 접근 권한|
|3|1|하드링크의 개수|
|4|root|파일 소유자의 로그인 ID|
|5|root|파일 소유자의 그룹 이름|
|6|213|파일의 크기(바이트 단위)|
|7|12 21 09:06|파일이 마지막으로 수정된 날짜|
|8|/etc/hosts|파일명|

- [1] 파일의 종류는 `file 파일명` 명령으로 확인 가능하다.
- [4] 리눅스의 모든 파일은 소유자가 있다. 시스템과 관련된 파일은 대부분 root 계정이 소유자이고, 일반 파일들은 해당 파일을 생성한 사용자가 소유자이다.
- [5] 리눅스의 사용자는 기본적으로 하나 이상의 그룹에 속해있다. 사용자가 속한 기본 그룹은 시스템 관리자만 관리 가능하다. 그룹이 정의된 파일은 /etc/group으로 시스템 관리자만 수정 가능하다. 사용자의 그룹을 알려주는 명령은 `groups 사용자명`이다.

## 파일의 접근 권한
### 접근권한의 종류
리눅스에서 접근권한은 읽기, 쓰기, 실행 권한으로 3가지로 이루어져 있다.
"파일"에 한하여, 각 접근 권한은 다음과 같이 해석된다.
- 읽기 : 파일을 읽거나 복사할 수 있다.
- 쓰기 : 파일을 수정, 이동, 삭제할 수 있다.
- 실행 : (셸 스크립트나 실행 파일의 경우)파일을 실행할 수 있다.
### 접근권한 표기방법
- 사용자 카테고리별로 누가 파일을 읽고, 쓰고, 실행할 수 있는지 문자로 표기한다.
    - 소유자 : 파일의 소유자로 일반적으로 파일을 생성한 사용자
    - 그룹 : 일반적으로 소유자가 속한 기본 그룹으로 그룹에 속한 사용자
    - 기타 사용자 : 소유자도아니고 그룹에 속한 사용자도 아닌 나머지 사용자
- 읽기권한은 r, 쓰기권한은 w, 실행권한은 x로 표기, 해당 권한이 없는 경우 -로 표기
- 사용자 카테고리별로 세가지 권한의 부여 여부를 rwx와 같이 묶어 세문자로 표기한다.   
### 접근권한 변경 명령
chmod 명령으로 접근 권한을 변경할 때 기호모드와 숫자모드를 사용할 수 있다.

## 기호를 이용한 파일 접근 권한 변경
> chmod [기호모드] 파일명  
> 기호모드는 사용자카테고리문자, 연산자기호, 접근권한문자로 구성된다.

<table>
  <tr>
    <td>구분</td>
    <td>문자/기호</td>
    <td>의미</td>
  </tr>
  <tr>
    <td rowspan="4">사용자 카테고리 문자</td>
    <td>u</td>
    <td>파일 소유자</td>
  </tr>
  <tr>
    <td>g</td>
    <td>소유자가 속한 그룹</td>
  </tr>
  <tr>
    <td>o</td>
    <td>소유자와 그룹 이외의 기타 사용자</td>
  </tr>
  <tr>
    <td>a</td>
    <td>전체 사용자</td>
  </tr>
  <tr>
    <td rowspan="3">연산자 기호</td>
    <td>+</td>
    <td>권한 부여</td>
  <tr>
    <td>-</td>
    <td>권한 제거</td>
  <tr>
    <td>=</td>
    <td>권한 설정</td>
  <tr>
    <td rowspan="3">접근 권한 문자</td>
    <td>r</td>
    <td>읽기권한</td>
  </tr>
  <tr>
    <td>w</td>
    <td>쓰기권한</td>
  </tr>
  <tr>
    <td>x</td>
    <td>실행권한</td>
  </tr>
</table>

## 숫자를 이용한 파일 접근 권한 변경
> chmod [숫자모드] 파일명  
> chmod 숫자(0~7)숫자(0~7)숫자(0~7) 파일명  
> 이 숫자는 r(4, 읽기), w(2, 쓰기), x(1, 실행)의 조합 합이다.


## 기본 접근 권한 설정
리눅스에서는 파일이나 디렉터리를 생성할 때 기본 접근 권한이 자동적으로 설정된다.
> umask  
> 기본 접근 권한을 출력한다.  
> 우분투의 기본 마스크 값은 022이다.
```
user1@ubuntu ~ % umask
022
```
### 간단히 마스크 값 계산하는 방법
최대 접근 권한에서 마스크 값을 빼면된다.
|마스크 값|일반 파일|디렉터리|
|---|---|---|
|002|664|775|
|077|600|700|
|027|640|750|


## 특수 접근 권한
- SetUID : 해당 파일이 실행되는 동안에는 파일을 실행한 사용자의 권한이 아니라 파일 소유자의 권한으로 실행할 수 있도록 한다.
- SetGID : 해당 파일이 실행되는 동안에는 파일을 실행한 사용자의 권한이 아니라 파일 소유 그룹 권한으로 실행할 수 있도록 한다.
- 스티키 비트 : 디렉터리에 스티키 비트가 설정되어 있으면 이 디렉터리에는 누구나 파일을 생성할 수 있으며, 파일을 생성한 계정으로 소유자가 설정되고 다른 사용자가 생성한 파일은 삭제할 수 없다.