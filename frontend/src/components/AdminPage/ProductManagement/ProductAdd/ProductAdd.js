import React, { useState } from 'react';

function ProductAdd() {
  const [formData, setFormData] = useState({
    productCode: '',
    productName: '',
    category: '',
    subCategory: '',
    description: '',
    salePrice: '',
    stock: '',
    saleRegistered: '',
    manufacturerId: '',
  });

  const [imageFile, setImageFile] = useState(null); // 이미지 파일 상태

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleFileChange = (e) => {
    setImageFile(e.target.files[0]); // 파일 설정
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    const formDataToSend = new FormData();
    formDataToSend.append(
      'store',
      new Blob([JSON.stringify(formData)], { type: 'application/json' })
    );
    if (imageFile) {
      formDataToSend.append('image', imageFile); // 이미지 파일 추가
    }

    fetch('/api/stores', {
      method: 'POST',
      body: formDataToSend,
    })
      .then((response) => {
        if (response.ok) {
          alert('상품 등록 성공!');
          setFormData({
            productCode: '',
            productName: '',
            category: '',
            subCategory: '',
            description: '',
            salePrice: '',
            stock: '',
            saleRegistered: '',
            manufacturerId: '',
          });
          setImageFile(null); // 이미지 파일 초기화
        } else {
          response.text().then((text) => {
            alert(`상품 등록 실패: ${text}`);
          });
        }
      })
      .catch((error) => console.error('상품 등록 실패:', error));
  };

  return (
    <div>
      <h2>상품 등록</h2>
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          name="productCode"
          placeholder="상품 코드"
          value={formData.productCode}
          onChange={handleChange}
          required
        />
        <input
          type="text"
          name="productName"
          placeholder="상품명"
          value={formData.productName}
          onChange={handleChange}
          required
        />
        <input
          type="text"
          name="category"
          placeholder="카테고리"
          value={formData.category}
          onChange={handleChange}
          required
        />
        <input
          type="text"
          name="subCategory"
          placeholder="상세 카테고리"
          value={formData.subCategory}
          onChange={handleChange}
        />
        <textarea
          name="description"
          placeholder="상품 설명"
          value={formData.description}
          onChange={handleChange}
          required
        />
        <input
          type="number"
          name="salePrice"
          placeholder="판매가"
          value={formData.salePrice}
          onChange={handleChange}
          required
        />
        <input
          type="number"
          name="stock"
          placeholder="재고"
          value={formData.stock}
          onChange={handleChange}
          required
        />
        <input
          type="text"
          name="manufacturerId"
          placeholder="제조사 ID"
          value={formData.manufacturerId}
          onChange={handleChange}
          required
        />
        <input type="file" onChange={handleFileChange} required /> {/* 이미지 업로드 */}
        <button type="submit">등록</button>
      </form>
    </div>
  );
}

export default ProductAdd;
