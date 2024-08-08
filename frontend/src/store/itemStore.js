import {create} from 'zustand';
import rug1 from '../assets/myroom/items/rug1.png';
import rug2 from '../assets/myroom/items/rug2.png';
import shelf1 from '../assets/myroom/items/shelf1.png';
import shelf2 from '../assets/myroom/items/shelf2.png';
import stand1 from '../assets/myroom/items/stand1.png';
import stand2 from '../assets/myroom/items/stand2.png';
import bed1 from '../assets/myroom/items/bed1.png';
import bed2 from '../assets/myroom/items/bed2.png';
import minitable1 from '../assets/myroom/items/minitable1.png';
import minitable2 from '../assets/myroom/items/minitable2.png';
import vase1 from '../assets/myroom/items/vase1.png';
import vase2 from '../assets/myroom/items/vase2.png';
import bigtable1 from '../assets/myroom/items/bigtable1.png';
import bigtable2 from '../assets/myroom/items/bigtable2.png';
import coin1 from '../assets/payment/coin1.png'
import coin2 from '../assets/payment/coin2.png'
import coin3 from '../assets/payment/coin3.png'
import coin4 from '../assets/payment/coin4.png'

const imageMap = {
    rug: [rug1, rug2],
    shelf: [shelf1, shelf2],
    stand: [stand1, stand2],
    bed: [bed1, bed2],
    minitable: [minitable1, minitable2],
    vase: [vase1, vase2],
    bigtable: [bigtable1, bigtable2],
};

const useItemStore = create((set) => ({
    items : [],
    images : {},
    histories : [],
    coins : [coin1, coin2, coin3, coin4],
    userInventory : {},
    hasitemids : {},
    selectedItems : {},
    setSelectedItems : (itemName, variantIndex) => set((state) => ({
        selectedItems: {
          ...state.selectedItems,
          [itemName]: variantIndex,
        },
    })),
    addToInventory: (itemName, variantIndex) => set((state) => ({
        userInventory: {
          ...state.userInventory,
          [itemName]: [...(state.userInventory[itemName] || []), variantIndex],
        },
    })),
    setItems: (newItems) => set((state) => ({
        items: newItems,
    })),
    setPrices: (newPrices) => set((state) => ({
        prices: newPrices,
    })),
    generateImagesFromNames: (itemNames) => set((state) => {
        const processedImages = {};
    
        itemNames.forEach((itemName) => {
            const baseName = itemName.replace(/[0-9]/g, ''); 
            if (imageMap[baseName]) {
                const variantIndex = parseInt(itemName.replace(/\D/g, '')) - 1;
                if (!processedImages[baseName]) {
                    processedImages[baseName] = [];
                }
                processedImages[baseName][variantIndex] = imageMap[baseName][variantIndex];
            }
        });
    
        return { images: processedImages };
    }),
    setPurchaseHistory: (purchaseHistory) => set((state) => {
        const processedHistory = purchaseHistory.map(event => ({
            ...event,
            date: new Date(event.transactionAt),
            isPurchase : true,
        }));

        const uniqueMap = new Map();

        [...state.histories, ...processedHistory].forEach(item => {
            uniqueMap.set(item.transactionAt, item);
        });

        const updatedHistory = Array.from(uniqueMap.values());

        return {
            histories: updatedHistory,
        };
    }),
    setPaymentHistory: (paymentHistory) => set((state) => {
        const processedHistory = paymentHistory.map(event => ({
            ...event,
            date: new Date(event.paymentAt),
            isPurchase : false,
        }));

        const uniqueMap = new Map();

        [...state.histories, ...processedHistory].forEach(item => {
            uniqueMap.set(item.paymentAt, item);
        });

        const updatedHistory = Array.from(uniqueMap.values());

        return {
            histories: updatedHistory,
        };
    }),
    setUserInventory: (inventory) => set((state) => {
        const newUserInventory = {};
        const newSelectedItems = {};
        const newhasitemids = {};

        inventory?.forEach((item) => {
           
            const baseName = item.itemName.replace(/[0-9]/g, '');  
            const variantIndex = parseInt(item.itemName.replace(/\D/g, '')); 
    
            if (newUserInventory[baseName]) {
                newUserInventory[baseName].push(variantIndex);
                newUserInventory[baseName].sort((a,b) => (a - b))
            } else {
                newUserInventory[baseName] = [variantIndex];
            }

            newhasitemids[item.itemName] = item.hasItemId

            if (item.equippedYn) {
                newSelectedItems[baseName]  = variantIndex;
            }
        });

        return { userInventory: newUserInventory,
            selectedItems: newSelectedItems,
            hasitemids: newhasitemids
         };

    }),

}))

export default useItemStore;