### 목차
- [리눅스 파일 시스템의 종류](#리눅스-파일-시스템의-종류)
- [파일 시스템 마운트](#파일-시스템-마운트)
- [디스크 추가 설치](#디스크-추가-설치)
- [디스크 관리](#디스크-관리)


## 리눅스 파일 시스템의 종류
파일 시스템은 운영체제의 주요한 기능 중 하나로, 다양한 정보를 가지고 있는 파일과 디렉터리의 집합을 구조적으로 관리하는 체계이다.
### 리눅스 고유의 디스크 기반 파일 시스템
- ext(ext1)  
초기 미닉스 파일 시스템을 대체하여 개발된 최초의 리눅스 고유 디스크 기반 파일 시스템
- ext2, ext3, ext4  
ext 파일 시스템의 문제점을 보완하여 개선된 파일 시스템
### 특수 용도의 가상 파일 시스템
디스크가 아니라 메모리에서 새엇ㅇ되어 사용되는 가상 파일 시스템이 있다. 가상 파일 시스템은 특수한 용도를 가지고 필요에 따라 생겼다 없어졌다 한다.
- swap : 스왑영역을 관리하기 위한 스왑파일시스템
- tmps, proc, ramfs, rootfs 등
### 현재 시스템이 지원하는 파일 시스템의 종류 확인하기  
/proc/filesystems 파일에서 확인 가능하다.


## 파일 시스템 마운트
### 마운트
파일 시스템과 디렉터리 계층 구조를 연결하는 것을 의미한다.
### 마운트 포인트
디렉터리 계층 구조에서 파일 시스템이 연결되는 디렉터리를 의미한다.
### 마운트 설정 파일
/etc/fstab 파일이 마운트 설정 정보를 가지고 있고, 부팅할 때 이 파일을 읽고 설정 내용에 따라 파일 시스템을 자동으로 마운트 한다.
- /etc/fstab 파일은 다음 예시와 같이 6가지 항목으로 구성되어 있다.
```
root@d1-dev-bastion:/# cat /etc/fstab
# /etc/fstab: static file system information.
#
# Use 'blkid' to print the universally unique identifier for a
# device; this may be used with UUID= as a more robust way to name devices
# that works even if disks are added and removed. See fstab(5).
#
# <file system> <mount point>   <type>  <options>       <dump>  <pass>
/dev/disk/by-uuid/1dd64d68-41ca-4bd5-a37b-08c18f73ac03 none swap sw 0 0
# / was on /dev/vdb4 during curtin installation
/dev/disk/by-uuid/3b06853e-b1a6-4319-81d5-7c2d9ce83808 / ext4 defaults 0 0
# /boot was on /dev/vdb2 during curtin installation
/dev/disk/by-uuid/fe826b95-a233-4e96-b6ce-474207ff1c0f /boot ext4 defaults 0 0
```
- 장치명 : 파일 시스템이 구축된 물리적인 디스크 장치의 이름
- 마운트 포인트 : 파일 시스템이 마운트 될 마운트 포인트
- 파일 시스템의 종류
- 옵션 : 파일 시스템의 속성(블록그룹)
- 덤프 관련 설정 : 0은 dump 명령으로 파일 시스템 내용이 덤프되지 않는 파일, 1은 데이터 백업 등을 위해 dump 명령이 사용 가능한 파일 시스템
- 파일 점검 옵션 : 0은 부팅할 때 fsck 명령으로 파일 시스템을 점검하지 않는다. 1은 루트 파일 시스템을 점검, 2는 루트 파일 시스템 이외의 파일 시스템을 점검한다.
### 마운트 명령
> mount [옵션] 장치명 마운트포인트  
> 파일 시스템을 마운트 한다.
>> 사용 예  
>> mount  
>> mount /dev/sdb1 /  
>> mount -t iso9660 /dev/cdrom /mnt/cdrom
- mount 명령만 사용하는 경우
현재 마운트되어 있는 정보가 출력된다. 해당 정보는 /etc/mtab 파일의 내용과 동일하다.
> unmount [옵션] 장치명 또는 마운트포인트  
> 파일 시스템을 언마운트 한다.

## 디스크 추가 설치
디스크를 추가로 장착하고 포맷하여 마운트하고 사용하는 방법은 다음 단계로 이루어진다.
> 새 디스크 장착하기 -> 디스크 파티션 나누기 -> 파티션에 파일시스템 생성하기(포맷) -> 디스크 마운트
### 디스크 파티션 나누기
하나의 디스크를 독립된 영역으로 구분하는 것을 파티션이라고 한다.
- 디스크 순서에 따라 다음과 같이 알파벳이 추가된다.
    - /dev/sda : 첫째 디스크
    - /dev/sdb : 둘째 디스크
    - /dev/sdc : 셋째 디스크
- 파티션은 디스크 장치 이름의 뒤에 숫자를 붙여서 표시한다.  
디스크 전체를 하나의 파티션으로 사용하려고 할 때도 파티션 작업은 반드시 해야 한다.
    - /dev/sda : 첫째 디스크 전체를 의미하는 장치의 이름
    - /dev/sda0 : 디스크의 첫째 파티션
    - /dev/sda1 : 디스크의 둘째 파티션
- 디스크를 파티션으로 나누는 작업은 fdisk 명령으로 한다.
> fdisk [옵션] 장치명  
> fdisk 명령은 root 계정에서 사용할 수 있다.  
>> 사용 예  
>> fdisk -l : 전체 디스크의 파티션 정보 확인
>> fdisk /dev/sdb : /dev/sdb 디스크를 파티션으로 나눈다.
### 파티션에 파일시스템 생성하기(포맷)
> mkfs [옵션] 장치명  
> 리눅스 파일 시스템을 만든다.  
> 옵션 -t : 파일 시스템의 종류를 지정(기본 ext2)  
>> 사용 예  
>> mkfs /dev/sdb1  
>> mkfs -t ext4 /dev/sdb1

> mke2fs [옵션] 장치명  
> 리눅스 개정판 확장 파일 시스템(ext2, ext3, ext4)을 만든다.
### 디스크 마운트
마운트 포인트로사용할 디렉터리 생성하여 준비하여 파일시스템을 마운트 하면 된다.  
마운트를 해제하게되면 파티션과의 연결이 끊어지므로 마운트한 파일 시스템을 볼 수 없다.
### 여러 디스크를 하나처럼 사용하기
디스크 용량이 부족할 때 여러개의 디스크를 하나의 디스크처럼 사용할 수 있다. LVM(Logical Volume Manager)은 파티션을 효율적으로 사용할 수 있도록 해주는 관리 도구로, 한 파티션의 용량이 부족할 때 다른 파티션으로 연장하여 사용할 수 있다.

## 디스크 관리
### 디스크 사용량 확인하기
파일 시스템별로 전체 사용량을 확인하는 df명령과 디렉터리별로 디스크의 사용량을 확인하는 du 명령이 있다.
> df [옵션] [파일 시스템]  
> 디스크의 남은 공간에 대한 정보를 출력  
> 옵션 -a : 모든 파일 시스템을 대상으로 디스크 사용량 확인
> 옵션 -h : 알기 쉬운 단위(GB, MB, KB 등)로 출력  
> 옵션 -T : 파일 시스템의 종류도 출력한다.
```
ubuntu@d1-dev-bastion:~$ df -hT
Filesystem     Type      Size  Used Avail Use% Mounted on
udev           devtmpfs  461M     0  461M   0% /dev
tmpfs          tmpfs      99M   12M   87M  12% /run
/dev/vda4      ext4       47G  5.8G   38G  14% /
tmpfs          tmpfs     493M     0  493M   0% /dev/shm
tmpfs          tmpfs     5.0M     0  5.0M   0% /run/lock
tmpfs          tmpfs     493M     0  493M   0% /sys/fs/cgroup
/dev/loop0     squashfs   90M   90M     0 100% /snap/core/8268
/dev/vda2      ext4      976M  291M  618M  32% /boot
tmpfs          tmpfs      99M     0   99M   0% /run/user/1001
tmpfs          tmpfs      99M     0   99M   0% /run/user/1000
```
> du [옵션] [디렉터리]  
> 특정 디렉터리별로 디스크의 사용 공간에 대한 정보를 출력한다.  
> 옵션 -s : 특정 디렉터리의 전체 사용량을 출력한다.  
> 옵션 -h : 알기 쉬운 단위(GB, MB, KB 등)로 출력
- 옵션을 지정하지 않으면 현재 디렉터리의 디스크 사용량 출력
```
ubuntu@d1-dev-bastion:~$ pwd
/home/ubuntu
ubuntu@d1-dev-bastion:~$ du
25948	./boan/smp
6712	./boan/itam
32664	./boan
52	./.ssh
787740	./logs
8	./.config/htop
12	./.config
4	./.gnupg/private-keys-v1.d
8	./.gnupg
4	./.cache
840868	.
```
- -s 옵션 사용시 서브 디렉터리나 파일의 용량은 표시하지 않고 지정한 디렉터리 전체 사용랴만 출력한다.
```
ubuntu@d1-dev-bastion:~$ du -s
841036	.
```
- 특정 사용자의 디스크 사용량을 알려면 해당 사용자의 홈 디렉터리를 지정한다.
```
ubuntu@d1-dev-bastion:~$ du -s ~ubuntu
841248	/home/ubuntu
```

### 파일 시스템 검사하고 복구하기
- fsck, e2fsck 명령
file system check의 약자로 inode 및 블록, 디렉터리, 파일 링크 등을 검사하고 필요 시 복구 작업도 수행한다.
