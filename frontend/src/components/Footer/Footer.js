import React from "react";
import "./Footer.css"; // 푸터 스타일링을 위한 CSS 파일

const Footer = () => {
  return (
    <footer className="footer">
      <div className="footer-container">
        <div className="footer-logo">
          <h2>아토피아</h2>
        </div>
        <div className="footer-links">
          <a href="#about">About Us</a>
          <a href="#services">Services</a>
          <a href="#contact">Contact</a>
          <a href="#privacy">Privacy Policy</a>
        </div>
        <div className="footer-copyright">
          <p>&copy; 2025 아토피아. All rights reserved.</p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
