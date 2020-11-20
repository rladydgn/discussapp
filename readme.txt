firebase

postData - 게시물 댓글(postData-comments)과 추천수(postData-voteRate)를 저장하는 디렉토리

userdata - 회원가입한 유저의 UId와 nickname을 저장하는 디렉토리


안드로이드 구현내용

Login, activity_login ---- 맨 처음 앱을 켰을 때 나오는 화면, 로그인 화면
onStart() -  logout 상태가 아니면 updataUI 호출

signIn() - 로그인, validateForm에서 로그인 형식이 유효한지 확인받고 firbase에 로그인 정보 전달. 존재하면 updataUI 호출
존재 하지 않으면 아이디 혹은 비밀번호가 틀렸다는 toast 출력

validateForm() - 로그인 형식이 올바른지 확인. 이메일, 또는 비밀번호가 비어있으면 textView에 에러 표시, return false
올바르면 return true

updateUI() - 호출받으면 확인위해 메세지 하나 출력, 로그인 성공 되기전에 마지막 확인절차. firebase에 회원가입 후 nickname이 존재
할 경우 로그인 성공, MainActivity로 이동. 회원가입할때 nickname 만들 때 앱 강제종료해서 nickname 없을경우 nickname생성 activity 호출

onSignInButtonClicked() - 로그인 버튼 클릭시 textView에 입력된 내용 담아서 signIn() 호출

onSignUpButtonClicked() - 회원가입 버튼 클릭시 회원가입 activity로 이동.


SignUpActivity, activity_sign_up --- login activity에서 회원가입 눌렀을 경우 이동, 회원가입 화면

validateForm() - 회원가입시 이메일이 비었는지, 패스워드가 비었는지, 패스워드가 다른지(두번입력) 확인 후 return (false or true)

createAccount() - validateForm() 호출 후 true 리턴받으면 계정 생성, firebase에 계정이 만들어짐, updateUI() 호출

onSignUpButtonClicked() - 회원가입 버튼 클릭시 textView에 입력된 내용 담아서 createAccount() 호출

updateUI() - 회원가입 완료시 아이디 생성하는 액티비티 호출.


ChooseIdPopuoActivity, activity_choose_id_popup_activity - nickname 생성하는 팝업창

onTouchEvent() - 바깥 레이어 클릭해도 닫히지 않게 함
onBackPressed() - 뒤로가기 눌러도 닫히지 않게 함
validateForm() - nickname이 유효한지 확인(2~10자)    *** 중복체크 미구현 ***
onIdButtonClicked() - 아이디 생성 버튼 클릭시 실행 firbase realtime database에 유저정보 저장, MainActivity 호출


MainActivity, activity_main --- 로그인 후 나오는 앱의 메인 화면, 유일한 로그아웃이 가능한 페이지

onStart() - updateUI() 호출

signOut() - 로그아웃후, 로그인 페이지로 이동

onLogoutButtonClicked() - 로그아웃 버튼 누르면 signOut() 호출

updateUI() - 투표 비율 설정. 투표수에 따라 조절되어야 함(*미완성*)

dpToPx() - dp를 px로 변경

onDayButtonClicked() - 일일토론 투표, 댓글 페이지로 이동


DayTalkActivity, activity_day_talk --- 토론 주제에 대해 투표와 댓글을 쓰는 페이지

onCreate() - firebase에서 UId nickname 가져오기. 엔터키 누르면 textView에 입력된
내용을 firebase에 저장하고 댓글창에 추가함.

setTextView() - 댓글창에 추가될 textVIew를 만들어주는 메소드

onClick() - 버튼이 눌렸을때 호출됨 찬성, 중립, 반대 버튼 눌릴경우 버튼 비활성화,
추천수, 버튼누른 UId를 fireBase에 저장함

dpToPx() - dp를 px로 변환

spToPx() - sp를 px로 변환

class CommentBackgroundThread - 댓글 불러오는 일을 서브스레드 하나 만들어서 실행시킴

class BackgroundThread - 투표수 불러오는 일을 서브스레드 하나 만들어서 실행시킴



기타

class Comment - 댓글 저장하는 클래스, 댓글 내용, 입력시간, 댓글 입력한 UId, 찬성중립반대

class User - 유저정보 저장하는 클래스, nickname

class Vote - 투표 수 저장하는 클래스, 총투표수, 찬성, 중립, 반대