import React, {useState} from 'react';
import { Button, TextField, Box, Typography } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import logo from '../../assets/logo/logo.png'
import kakaoLogo from '../../assets/login/kakao.png'; 
import googleLogo from '../../assets/login/google.png'; 
import LoginPageStyle from '../../css/login/Loginpage.module.css';

function LoginPage() {
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  const handleLogin = async () => {
    try {
      const response = await axios.post('/api/login', {
        username,
        password,
      });
      
      // 로그인 성공 시 JWT 토큰 저장
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('refreshtoken', response.data.refreshToken);
      navigate('/main');
    } catch (error) {
      alert('로그인 실패: 아이디 또는 비밀번호를 확인해주세요.');
    }
  };
  
  return (
    <div className={LoginPageStyle.root}>
      <img src={logo} alt="FLORINK" className={LoginPageStyle.logo} />
      <TextField
        className={LoginPageStyle.input}
        label="전화번호 또는 아이디"
        variant="outlined"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
      />
      <TextField
        className={LoginPageStyle.input}
        label="비밀번호"
        variant="outlined"
        type="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />
      <Button className={LoginPageStyle.customButton} variant="contained" onClick={handleLogin}>로그인</Button>
      <Box className={LoginPageStyle.linksContainer}>
        <Button className={LoginPageStyle.linkButton} onClick={()=>navigate('/FindAccount')}>아이디 찾기</Button>
        <Button className={LoginPageStyle.linkButton} onClick={()=>navigate('/FindAccount')}>비밀번호 찾기</Button>
        <Button className={LoginPageStyle.linkButton} onClick={()=>navigate('/signup')}>회원가입</Button>
      </Box>

      <hr className={LoginPageStyle.divider} />

      <Typography className={LoginPageStyle.snsLogin}>SNS 로그인</Typography>
      <Box className={LoginPageStyle.snsContainer}>
        <Button className={`${LoginPageStyle.snsButton} ${LoginPageStyle.kakaoButton}`} variant="contained">
          <img src={kakaoLogo} alt="Kakao" className={LoginPageStyle.snsLogo} /> 카카오로 계속
        </Button>
        <Button className={`${LoginPageStyle.snsButton} ${LoginPageStyle.googleButton}`} variant="contained">
          <img src={googleLogo} alt="Google" className={LoginPageStyle.snsLogo} /> 구글로 계속
        </Button>
      </Box>
    </div>
  );
}

export default LoginPage;
