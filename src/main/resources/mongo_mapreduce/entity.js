{
    business: {
     
        equity: {desc: "投资人及出资信息",
            members: [
            {label:"投资人", type:"string"},
            {label:"出资金额", type:"currency"},
            {label:"出资时间", type:"date"}
        ]
        },
        award: { desc: "奖项",
            members: [
            {label:"证书编号", type: "id"},
            {label:"奖项名称", type: "string"},
            {label:"颁发机构", type: "string"},
            {label:"颁发时间", type: "date"},
        ]
        },
        employee: {desc: "职员",
            members: [
            {label:"姓名", type: "full_name"},
            {label:"部门", type:"string"},
            {label:"职务", type: "job"},
            {label:"入职时间", type: "date"},
            {label:"离职时间", type: "date"}
        ]
        },


        loan: {
            equity: { desc: "股权出质",
                members: [
                {label:"登记编号", type:"id"},
                {label:"出质人", type:"string"},
                {label:"证照/证件号码", type:"id"},
                {label:"出质股权数额", type:"currency"},
                {label:"质权人", type:"string"},
                {label:"证照/证件号码", type:"id"},
                {label:"股权出质设立登记日期", type:"date"}
            ]
            },
            property: {
                movable: { desc: "动产出质",
                    members: [
                    {label:"登记编号", type:"id"},
                    {label:"登记日期", type:"date"},
                    {label:"登记机关", type:"string"},
                    {label:"被担保债权数额", type:"currency"}
                ]
                },
                immovable: { desc: "不动产出质",
                    members: [

                ]
                }
            }
        },
        trade: {
            international: { desc: "国际贸易",
                members: [
                {label: "统计标识", type: "id"},
                {label: "经营单位", type: "id"},
                {label: "运输方式", type: "id"},
                {label: "监管代码", type: "id"},
                {label: "起抵国别", type: "id"},
                {label: "产终国别", type: "id"},
                {label: "手册编号", type: "id"},
                {label: "结关日期", type: "date"},
                {label: "商品编码", type: "id"},
                {label: "附加编码", type: "id"},
                {label: "第一数量", type: "amount"},
                {label: "第一单位", type: "id"},
                {label: "第二数量", type: "amount"},
                {label: "第二单位", type: "id"},
                {label: "美元金额", type: "currency"}
            ]
            },
            internal: { desc: "国内贸易",
                members: [
                {label:"合同编号", type:"id"},
                {label:"出卖人", type:"string"},
                {label:"买受人", type:"string"},
                {label:"签订日期", type:"date"},
                {label:"标的物名称", type:"string"},
                {label:"规格型号", type:"string"},
                {label:"生产厂家", type:"string"},
                {label:"计量单位", type:"unit"},
                {label:"单价", type:"amount"}
            ]
            }

        }
    },
    people: {

        relation: { desc: "社会关系",
            members: [
            {label:"关系类型", type: "string"},
            {label:"关系人姓名", type: "full_name"}
        ]
        },


    },
    product: { desc: "产品",
        members: [
            {label: "品牌", type: "string"},
            {label: "系列", type: "string"},
            {label: "型号", type: "string"}
        ]
    }
}
