import React, { useState } from 'react';
import './ProductRegistration.css';

function ProductRegistration() {
  const [formData, setFormData] = useState({
    prodCode: '',
    prodName: '',
    prodOriginPrice: '',
    prodSalePrice: '',
    prodCategory: '',
    prodCategoryDetail: '',
    prodDesc: '',
    prodMainImgPath: '',
    prodQuantity: '',
    prodSalesDist: '',
    originId: '',
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    // 상품 등록 API 호출
    console.log('상품 등록:', formData);
  };

  return (
    <div className="product-registration">
      <h2>상품 등록</h2>
      <form onSubmit={handleSubmit} className="product-form">
        <input type="text" name="prodCode" placeholder="상품 코드" value={formData.prodCode} onChange={handleChange} required />
        <input type="text" name="prodName" placeholder="상품명" value={formData.prodName} onChange={handleChange} required />
        <input type="number" name="prodOriginPrice" placeholder="원가" value={formData.prodOriginPrice} onChange={handleChange} required />
        <input type="number" name="prodSalePrice" placeholder="판매가" value={formData.prodSalePrice} onChange={handleChange} required />
        <input type="text" name="prodCategory" placeholder="상품 분류" value={formData.prodCategory} onChange={handleChange} required />
        <input type="text" name="prodCategoryDetail" placeholder="상품 상세 분류" value={formData.prodCategoryDetail} onChange={handleChange} />
        <textarea name="prodDesc" placeholder="상품 설명" value={formData.prodDesc} onChange={handleChange} required></textarea>
        <input type="file" name="prodMainImgPath" onChange={(e) => setFormData({ ...formData, prodMainImgPath: e.target.files[0] })} />
        <input type="number" name="prodQuantity" placeholder="재고" value={formData.prodQuantity} onChange={handleChange} required />
        <input type="text" name="prodSalesDist" placeholder="판매 등록 여부" value={formData.prodSalesDist} onChange={handleChange} required />
        <input type="text" name="originId" placeholder="제조사 ID" value={formData.originId} onChange={handleChange} required />
        <button type="submit">상품 등록</button>
      </form>
    </div>
  );
}

export default ProductRegistration;
